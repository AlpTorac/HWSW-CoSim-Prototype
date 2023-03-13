import hwsim_evaluation_object

import evaluation_hardware_simulator

import hardware_simulator_mosaik_API
import mosaik_api

hardware_simulator_eval_output_file_name = 'hardware_simulator_eval_output_file'
"""_summary_
The path, where the file will be created, in which this evaluation class
will generate its output.
"""
class EvaluationHardwareSimulatorMosaikAPI(hardware_simulator_mosaik_API.HardwareSimulatorMosaikAPI, hwsim_evaluation_object.HWSIMEvaluationObject):
    """_summary_
    This class is to be used to measure run times of the methods of
    hardware_simulator_mosaik_API.HardwareSimulatorMosaikAPI.
    """
    
    def __init__(self):
        hardware_simulator_mosaik_API.HardwareSimulatorMosaikAPI.__init__(self)
        self.hardware_simulator_eval_output_file = None
        self.start_time = None
        self.end_time = None
    
    def get_full_method_name(self, method_name):
        return 'EvaluationHardwareSimulatorMosaikAPI.'+method_name

    def init(self, sid, time_resolution, **sim_params):
        self.start_time = self.get_current_system_time()
        
        if hardware_simulator_eval_output_file_name in sim_params:
            self.hardware_simulator_eval_output_file = sim_params[hardware_simulator_eval_output_file_name]

        return self.add_time_measurement(self, 'init',
            hardware_simulator_mosaik_API.HardwareSimulatorMosaikAPI.init,
            sid=sid, time_resolution=time_resolution, **sim_params)

    def init_simulator(self):
        return evaluation_hardware_simulator.EvaluationHardwareSimulator()

    def create(self, num, model, **model_params):
        return self.add_time_measurement(self, 'create',
            hardware_simulator_mosaik_API.HardwareSimulatorMosaikAPI.create,
            num=num, model=model, **model_params)

    def get_data(self, outputs):
        return self.add_time_measurement(self, 'get_data',
            hardware_simulator_mosaik_API.HardwareSimulatorMosaikAPI.get_data,
            outputs=outputs)

    def step(self, time, inputs, max_advance):
        return self.add_time_measurement(self, 'step',
            hardware_simulator_mosaik_API.HardwareSimulatorMosaikAPI.step,
            time=time, inputs=inputs, max_advance=max_advance)
    
    def finalize(self):
        self.end_time = self.get_current_system_time()
        self.write_output(self.hardware_simulator_eval_output_file,
                          'Hardware simulator time measurements:\n',
                          '\nHardware simulator ran for: %.0f\n' % (self.end_time - self.start_time))

if __name__ == '__main__':
    mosaik_api.start_simulation(EvaluationHardwareSimulatorMosaikAPI())