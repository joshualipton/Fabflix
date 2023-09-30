import os

def calculate_average(column_values):
    if len(column_values) == 0:
        return 0
    return sum(column_values) / len(column_values)

def convert_to_milliseconds(nanoseconds):
    return nanoseconds / 1000000

# Get the file names from the user
file_names = input("Enter the file names (separated by space): ").split()

# Get the current directory
directory = os.path.dirname(os.path.abspath(__file__))

# Initialize lists to store all TJ and TS values
all_tj_values = []
all_ts_values = []

# Process each file
for filename in file_names:
    # Construct the full path to the file
    file_path = os.path.join(directory, filename)

    # Check if the file exists
    if not os.path.isfile(file_path):
        print(f"File '{filename}' does not exist.")
        continue

    # Initialize lists to store TJ and TS values for the current file
    tj_values = []
    ts_values = []

    # Open the file for reading
    with open(file_path, 'r') as file:
        # Read the lines of the file
        lines = file.readlines()

    # Process each line of the file
    for line in lines:
        # Split the line into TJ and TS values
        tj, ts = line.strip().split(' ')

        # Convert the values to integers
        tj = int(tj)
        ts = int(ts)

        # Convert nanoseconds to milliseconds
        tj_ms = convert_to_milliseconds(tj)
        ts_ms = convert_to_milliseconds(ts)

        # Add the values to the respective lists
        tj_values.append(tj_ms)
        ts_values.append(ts_ms)

    # Extend the all_tj_values and all_ts_values lists with the values from the current file
    all_tj_values.extend(tj_values)
    all_ts_values.extend(ts_values)

# Calculate the average of all TJ and TS values
tj_average = calculate_average(all_tj_values)
ts_average = calculate_average(all_ts_values)

# Print the overall averages
print(f"TJ Average: {tj_average} ms")
print(f"TS Average: {ts_average} ms")
