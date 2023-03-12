import argparse

def get_options(**defaults):
    """_summary_

    A helper method that makes use of the argparse package and
    can be used to read binary options from the terminal.
    
    Args:
        defaults (_type_): A dict with key = option name,
        value = option value

    Returns:
        _type_: The parsed options
    """
    
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
    
    if binary_args is not None:
        # Flatten binary_args and remove the leading and trailing quotation marks
        binary_args = [arg.lstrip('\"\'').rstrip('\"\'') for sublist in binary_args for arg in sublist]

    return (options, binary_args, binary_path)