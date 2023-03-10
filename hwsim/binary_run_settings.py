class BinaryRunSettings():
    """_summary_
    This class contains all flags/options that a binary can be
    run with in form of a dict
    (key = name of the option, value = how it can be used
    from the terminal)
    """
    
    def __init__(self, *option_names):
        self.option_dict = {}
        """_summary_
        Contains all flags/options that a binary can be run with
        (key = name of the option, value = how it can be used
        from the terminal)
        """
        self.add_initial_options()
        self.add_options(*option_names)
        
    def add_initial_options(self):
        """_summary_
        Add initial options to self.option_dict
        """
        self.add_option(self.get_binary_path_option_name())
        self.add_option(self.get_binary_arg_option_name())
        self.add_option(self.get_output_path_option_name())
    
    def get_binary_path_option_name(self):
        """_summary_
        Returns:
            _type_: The name of the option, with which the
            binary path will be passed to binary from terminal
        """
        return 'binary_path'
    
    def get_output_path_option_name(self):
        """_summary_
        Returns:
            _type_: The name of the option, with which the
            output directory path for generating outputs
            will be passed to binary from terminal
        """
        return 'outdir'
        
    def get_binary_arg_option_name(self):
        """_summary_
        Returns:
            _type_: The name of the option, with which the
            binary arguments will be passed to binary from
            terminal
        """
        return 'binary_arg'
    
    def get_option_notation(self, option_name):
        """_summary_
        Args:
            option_name (_type_): A given option name
        Returns:
            _type_: How it can be used
        from the terminal
        """
        return self.option_dict[option_name]

    def get_option_dict(self):
        return self.option_dict

    def to_flag(self, option_name):
        """_summary_
        Args:
            option_name (_type_): A given option name to add
        Returns:
            _type_: How it can be used
        from the terminal
        """
        return "--%s" % option_name

    def add_option(self, option_name):
        """_summary_
        Adds the given option_name to self.option_dict
        
        Args:
            option_name (_type_): A given option name to add
        """
        if option_name not in self.option_dict.keys():
                self.option_dict[option_name] = self.to_flag(option_name)

    def add_options(self, *option_names):
        """_summary_
        Adds the given option_name to self.option_dict
        
        Args:
            option_names (_type_): Option names to add
        """
        for option_name in option_names:
            self.add_option(option_name)

    def convert_options(self, options):
        """_summary_
        Transforms the given option_name, option_value pairs
        to a list of strings, which will be passed to the terminal.
        
        Args:
            options (_type_): A data structure, which contains
            option_name, option_value pairs

        Returns:
            _type_: The said list of strings, which will be passed to the terminal
        """
        result = []

        if options is not None:
            for option_name, option_value in options:
                self.add_option(option_name)
                result.append("%s=\"%s\"" % (self.option_dict[option_name], str(option_value)))
        
        return result

    def convert_to_option(self, option_name, option_values):
        """_summary_
        Transforms the given option_name, option_value pairs
        to a list of strings, which will be passed to the terminal.
        
        Args:
            option_name (_type_): The name of the option in self.option_dict
            option_values (_type_): A list of values

        Returns:
            _type_: The said list of strings, which will be passed to the terminal
        """
        result = []

        if option_name is not None and option_values is not None:
            self.add_option(option_name)
            for option_value in option_values:
                result.append("%s=\"%s\"" % (self.option_dict[option_name], str(option_value)))
        
        return result

    def convert_to_output_dir_option(self, option_value):
        """_summary_
        Transforms the given option_value for output directory path
        to a string, which will be passed to the terminal.
        
        Args:
            option_value (_type_): The path to the output directory

        Returns:
            _type_: The said string, which will be passed to the terminal
        """
        return "%s=%s" % (self.option_dict[self.get_output_path_option_name()], str(option_value))

    def convert_to_binary_path_option(self, option_value):
        """_summary_
        Transforms the given option_value for binary path
        to a string, which will be passed to the terminal.
        
        Args:
            option_value (_type_): The path to the binary to run

        Returns:
            _type_: The said string, which will be passed to the terminal
        """
        return "%s=%s" % (self.option_dict[self.get_binary_path_option_name()], str(option_value))

    def convert_to_binary_arg_option(self, option_values):
        """_summary_
        Transforms the given option_values as binary arguments
        to a list of strings, which will be passed to the terminal.
        
        Args:
            option_values (_type_): Values of binary arguments

        Returns:
            _type_: The said list of strings, which will be passed to the terminal
        """
        return self.convert_to_option(self.get_binary_arg_option_name(), option_values)
    
