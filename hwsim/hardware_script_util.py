import argparse

def get_options(**defaults):
    parser = argparse.ArgumentParser()
    parser.add_argument('--binary_path',
                    help='Path to the binary to execute.')
    parser.add_argument('--binary_arg', action='append', nargs='*',
                    help='List of binary arguments')

    if defaults is not None:
        for option, defaultValue in defaults.items():
            parser.add_argument('--'+option, default=defaultValue)

    options = parser.parse_args()
    dict_options = vars(options)
    binary_args = dict_options['binary_arg']
    binary_path = dict_options['binary_path']

    return (options, binary_args, binary_path)