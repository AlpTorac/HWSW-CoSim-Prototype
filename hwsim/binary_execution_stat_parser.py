import re

class BinaryExecutionStatParser():
    def __init__(self):
        # A pattern that matches the lines in the output, which are not related to
        # the system object (first few lines that do not start with "system.")
        #
        # Captures the name and the numerical value of the statistics
        #
        # Excludes commentary
        self.binary_execution_stats_pattern = re.compile('^(\w+)\s+((?:\d|\.)+)')

    def parse_execution_stats_from_file(self, current_output_dir):
        return self.parse_execution_stats(self.read_execution_stats_file(current_output_dir))

    def parse_execution_stats(self, stats_text):
        stats = {}

        for line in stats_text:
            stat = self.binary_execution_stats_pattern.match(line)

            if stat is not None:
                stat_name = stat.group(1)
                stat_value = stat.group(2)

                stats[stat_name] = stat_value

        return stats

    def read_execution_stats_file(self, current_output_dir):
        stats_file = open(self.get_execution_stats_file_path(current_output_dir), 'r')
        return stats_file.readlines()

    def get_execution_stats_file_path(self, current_output_dir):
        return current_output_dir + '/stats.txt'