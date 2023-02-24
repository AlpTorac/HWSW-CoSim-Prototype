import argparse

def get_options():
    parser = argparse.ArgumentParser()
    parser.add_argument("--binary_path",
                    help="Path to the binary to execute.")
    parser.add_argument("--binary_arg", action='append', nargs='*',
                    help="List of binary arguments")

    options = vars(parser.parse_args())
    binary_args = options['binary_arg']
    binary_path = options['binary_path']

    return (binary_args, binary_path)