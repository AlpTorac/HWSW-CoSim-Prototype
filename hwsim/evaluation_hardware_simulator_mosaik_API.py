import evaluation_object
import evaluation_hardware_simulator

import hardware_simulator_mosaik_API
import mosaik_api

import time

hardware_simulator_eval_output_file_name = 'hardware_simulator_eval_output_file'

class EvaluationHardwareSimulatorMosaikAPI(hardware_simulator_mosaik_API.HardwareSimulatorMosaikAPI, evaluation_object.EvaluationObject):
    def __init__(self):
        hardware_simulator_mosaik_API.HardwareSimulatorMosaikAPI.__init__(self)
        self.hardware_simulator_eval_output_file = None
        self.hardware_simulator_output_dir = None
        self.start_time = None
        self.end_time = None
    
    def get_full_method_name(self, method_name):
        return 'EvaluationHardwareSimulatorMosaikAPI.'+method_name

    def init(self, sid, time_resolution, **sim_params):
        if hardware_simulator_eval_output_file_name in sim_params:
            self.hardware_simulator_eval_output_file = sim_params[hardware_simulator_eval_output_file_name]

        self.start_time = time.time_ns()
        return self.add_time_measurement(self, 'init',
            hardware_simulator_mosaik_API.HardwareSimulatorMosaikAPI.init,
            sid=sid, time_resolution=time_resolution, **sim_params)

    def init_simulator(self):
        return evaluation_hardware_simulator.EvaluationHardwareSimulator()

    def create(self, num, model, gem5_run_command, gem5_output_path,
    hardware_script_run_command):
        
        self.hardware_simulator_output_dir = gem5_output_path

        return self.add_time_measurement(self, 'create',
            hardware_simulator_mosaik_API.HardwareSimulatorMosaikAPI.create,
            num=num, model=model, gem5_run_command=gem5_run_command, gem5_output_path=gem5_output_path,
                    hardware_script_run_command=hardware_script_run_command)

    def get_data(self, outputs):
        return self.add_time_measurement(self, 'get_data',
            hardware_simulator_mosaik_API.HardwareSimulatorMosaikAPI.get_data,
            outputs=outputs)

    def step(self, time, inputs, max_advance):
        return self.add_time_measurement(self, 'step',
            hardware_simulator_mosaik_API.HardwareSimulatorMosaikAPI.step,
            time=time, inputs=inputs, max_advance=max_advance)
    
    def finalize(self):
        self.end_time = time.time_ns()
        file = open(self.hardware_simulator_eval_output_file, 'x')
        
        file.write('Hardware simulator time measurements:\n')

        output_data = self.get_collector().reduce_time_measurements()
        for output_name, output_value in output_data.items():
            file.write(output_name+': '+str(output_value)+'\n')

        file.write('\nHardware simulator ran for: %.0f\n' % (self.end_time - self.start_time))

        file.close()

if __name__ == '__main__':
    mosaik_api.start_simulation(EvaluationHardwareSimulatorMosaikAPI())