

class BinaryRunSettings():
    def __init__(self):
        pass

    def get_binary_path_option(self, binary_path):
        return '--binary_path=' + binary_path

    def get_output_dir_option(self, current_output_dir):
        return '--outdir=' + current_output_dir

    def convert_binary_arguments_to_options(self, binary_arguments):
        args = ''

        if binary_arguments is not None:
            for arg in binary_arguments:
                args += '--binary_arg=' +'\"'+ arg + '\"' + ' '
        
        return args