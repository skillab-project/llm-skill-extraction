import pandas as pd
import json

INPUT_CSV = "data/esco_skills.csv"
OUTPUT_JSON = "data/esco_light.json"

def convert():
    print(f"Reading data from {INPUT_CSV}...")

    # Read only the preferred  labels from csv
    try:
        df = pd.read_csv(INPUT_CSV, usecols=['preferredLabel', 'altLabels', 'description'])
    except Exception as e:
        print(f"Failed to read {INPUT_CSV}: {e}")
        return

    # Fill the NaN values with empty space
    df = df.fillna("")

    # The alt labels is text with "\n", make it a list
    def parse_alt_labels(text):
        if text == "":
            return []

        # Split the text for every "\n"
        raw_list = text.split('\n')

        # Create a new clean list
        clean_list = []

        for item in raw_list:
            # Clean spaces
            clean_item = item.strip()

            # If there is something else that spaces, add it to the list
            if clean_item:
                clean_list.append(clean_item)

        return clean_list

    print("Parsing alt labels...")

    # Get the altLabels column and for every row use the function parse_alt_labels
    # and change the old text with list
    df['altLabels'] = df['altLabels'].apply(parse_alt_labels)

    # Save to JSON
    records = df.to_dict(orient='records')

    # Save to disk
    with open(OUTPUT_JSON, 'w', encoding='utf-8') as f:
        json.dump(records, f, ensure_ascii=False, indent=2)

    print("Conversion complete!")

convert()

