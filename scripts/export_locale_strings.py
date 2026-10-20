#!/usr/bin/env python3
"""Export every string resource to a spreadsheet for the translators, and import their edits back.

    python3 scripts/export_locale_strings.py                       # everything
    python3 scripts/export_locale_strings.py --since last.xlsx     # only keys not in an earlier export

Columns: Key | English | Burmese | Chinese | Placeholders | Source file | Missing

Plurals are exported as `name[quantity]`. Strings marked translatable="false" are skipped, except
`app_name`. A locale that only defines the `other` plural form (Burmese and Chinese have no
singular) is exported with that text for every quantity.

Needs `pip install openpyxl`. Languages are the values-* folders listed in LANGUAGES below; add a
row there when you add a language (see docs/LOCALIZATION.md).
"""
import argparse
import os
import re
import sys
import xml.etree.ElementTree as ET

import openpyxl
from openpyxl.styles import Alignment, Font, PatternFill

RES = "app/src/main/res"
# (column title, values folder, short code)
LANGUAGES = [("English", "values", "en"), ("Burmese", "values-my", "my"), ("Chinese", "values-zh", "zh")]
PLACEHOLDER = re.compile(r"%(?:\d+\$)?[-#+ 0,(]*\d*(?:\.\d+)?[sdfxXeEgGcb%]")


def unescape(text):
    text = (text or "").strip()
    if len(text) >= 2 and text[0] == '"' and text[-1] == '"':
        text = text[1:-1]
    return (text.replace("\\'", "'").replace('\\"', '"').replace("\\n", "\n")
            .replace("\\@", "@").replace("\\?", "?"))


def text_of(element):
    return unescape("".join(element.itertext()))


def collect(res):
    """-> (ordered keys, {key: {code: text}}, {key: source file})"""
    data, source, order = {}, {}, []
    files = sorted({f for _, folder, _ in LANGUAGES
                    for f in os.listdir(os.path.join(res, folder)) if re.match(r"strings.*\.xml$", f)},
                   key=lambda f: (f != "strings.xml", f))
    for filename in files:
        for _, folder, code in LANGUAGES:
            path = os.path.join(res, folder, filename)
            if not os.path.exists(path):
                continue
            for el in ET.parse(path).getroot():
                if el.tag == "string":
                    if el.get("translatable") == "false" and el.get("name") != "app_name":
                        continue
                    items = [(el.get("name"), text_of(el))]
                elif el.tag == "plurals":
                    items = [(f"{el.get('name')}[{i.get('quantity')}]", text_of(i)) for i in el]
                else:
                    continue
                for key, value in items:
                    if key not in data:
                        data[key], source[key] = {}, filename
                        order.append(key)
                    data[key][code] = value
    for key in order:
        match = re.match(r"(.+)\[(.+)\]$", key)
        if match:  # a locale with only "other" covers every quantity
            for _, _, code in LANGUAGES[1:]:
                if not data[key].get(code):
                    data[key][code] = data.get(f"{match.group(1)}[other]", {}).get(code, "")
    if "app_name" in data:  # not translated; shown as-is in every language
        for _, _, code in LANGUAGES[1:]:
            data["app_name"][code] = data["app_name"].get("en", "")
    return order, data, source


def main():
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--res", default=RES)
    parser.add_argument("--out", default="locale_strings.xlsx")
    parser.add_argument("--since", help="an earlier export; only keys missing from it are written")
    args = parser.parse_args()

    order, data, source = collect(args.res)
    if args.since:
        previous = {row[0] for row in openpyxl.load_workbook(args.since).active.iter_rows(min_row=2, values_only=True)}
        order = [key for key in order if key not in previous]

    workbook = openpyxl.Workbook()
    sheet = workbook.active
    sheet.title = "Strings"
    sheet.append(["Key"] + [name for name, _, _ in LANGUAGES] + ["Placeholders", "Source file", "Missing"])
    mismatches = []
    for key in order:
        values = data[key]
        english = [p for p in PLACEHOLDER.findall(values.get("en", "")) if p != "%%"]
        for _, _, code in LANGUAGES[1:]:
            found = sorted(p for p in PLACEHOLDER.findall(values.get(code, "")) if p != "%%")
            if values.get(code) and found != sorted(english):
                mismatches.append(f"{key} [{code}]")
        missing = [name for name, _, code in LANGUAGES[1:] if not values.get(code)]
        sheet.append([key] + [values.get(code) or None for _, _, code in LANGUAGES]
                     + [", ".join(dict.fromkeys(english)) or None, source[key], ", ".join(missing) or None])

    for cell in sheet[1]:
        cell.font = Font(bold=True, color="FFFFFFFF")
        cell.fill = PatternFill("solid", fgColor="FF1F7A4D")
        cell.alignment = Alignment(wrap_text=True, vertical="center")
    for row in sheet.iter_rows(min_row=2):
        for cell in row:
            cell.alignment = Alignment(wrap_text=True, vertical="top")
    for column, width in zip("ABCDEFG", [38, 55, 55, 45, 16, 17, 16]):
        sheet.column_dimensions[column].width = width
    sheet.freeze_panes = "B2"
    sheet.auto_filter.ref = sheet.dimensions
    workbook.save(args.out)

    print(f"{len(order)} rows -> {args.out}")
    if mismatches:
        print("Placeholder mismatch (a runtime crash waiting to happen):", ", ".join(mismatches))
        sys.exit(1)


if __name__ == "__main__":
    main()
