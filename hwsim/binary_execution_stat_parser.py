import re

class BinaryExecutionStatParser():
    """_summary_
    This class encapsulates the means to parse execution statistics from
    binary runs.
    """
    
    def __init__(self):
        self.binary_execution_stats_pattern = re.compile('^(\w+)\s+((?:\d|\.)+)')
        """_summary_
        A pattern that matches the lines in the output, which are not related to
        the system object (first few lines that do not start with "system.")
        
        Captures the name and the numerical value of the statistics
        
        Excludes commentary
        """

    def parse_execution_stats_from_file(self, current_output_dir, file_name='stats.txt'):
        """_summary_
        Parses the binary execution statistics from the given output
        directory path.
        
        Args:
            current_output_dir (_type_): The given output directory path
            file_name (_type_): The name of the binary execution statistics file

        Returns:
            _type_: A dict of binary execution statistics
            (key = statistic name, value = value to the said statistic)
        """
        return self.parse_execution_stats(self.read_execution_stats_file(current_output_dir, file_name))

    def parse_execution_stats(self, stats_text):
        """_summary_
        Parses the execution statistics from the given text.

        Args:
            stats_text (_type_): Text version of the execution statistics

        Returns:
            _type_: A dict of binary execution statistics
            (key = statistic name, value = value to the said statistic)
        """
        stats = {}

        for line in stats_text:
            stat = self.binary_execution_stats_pattern.match(line)

            if stat is not None:
                stat_name = stat.group(1)
                stat_value = stat.group(2)

                stats[stat_name] = stat_value

        return stats

    def read_execution_stats_file(self, current_output_dir, file_name='stats.txt'):
        """_summary_
        Reads the file with the name "file_name" at the given output directory path.

        Args:
            current_output_dir (_type_): The given output directory path
            file_name (_type_): The name of the binary execution statistics file

        Returns:
            _type_: The contents of the said file
        """
        stats_file = open(self.get_execution_stats_file_path(current_output_dir, file_name), 'r')
        return stats_file.readlines()

    def get_execution_stats_file_path(self, current_output_dir, file_name='stats.txt'):
        """_summary_
        Args:
            current_output_dir (_type_): The given output directory path
            file_name (_type_): The name of the binary execution statistics file
        Returns:
            _type_: The path to the file called file_name in current_output_dir
        """
        return current_output_dir + '/' + file_name