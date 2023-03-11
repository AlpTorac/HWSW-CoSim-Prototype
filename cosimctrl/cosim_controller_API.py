import mosaik_api
import json

modelName = 'CosimController'

transition_chain_file_path_field = "transition_chain_file_path"

transition_char_field = "transition_char"
transition_time_field = "transition_time"

class CosimControllerAPI(mosaik_api.Simulator):
    """_summary_
    This is a controller that implements mosaik_api.Simulator and sends
    predefined events to software simulation part when it is time.
    """

    meta = {
        'api_version': mosaik_api.__api_version__,
        'type': 'time-based',
        'models': {
            modelName: {
                'public': True,
                'params': [transition_chain_file_path_field],
                'attrs': [transition_char_field, transition_time_field]
            },
        },
    }
    """_summary_
    See :meth:`mosaik_api.Simulator.meta` for more information and the used
    format.
    """
    
    def __init__(self):
        __doc__ = mosaik_api.Simulator.__doc__
        super().__init__(CosimControllerAPI.meta)
        self.eid_prefix = ''
        self.transition_chain = None
        self.current_transition = None
        self.time = 0

    def init(self, sid, time_resolution, **sim_params):
        if 'eid_prefix' in sim_params:
            self.eid_prefix = sim_params['eid_prefix']
        if transition_chain_file_path_field in sim_params:
            self.prepare_transition_chain(sim_params[transition_chain_file_path_field])
        return self.meta

    def prepare_transition_chain(self, transition_chain_file_path):
        self.transition_chain = json.loads(open(transition_chain_file_path).read())

    def create(self, num, model, **model_params):
        entities = []

        eid = '%s%d' % (self.eid_prefix, 0)
        entities.append({'eid': eid, 'type': model})

        return entities

    def get_data(self, outputs):
        data = {}
        for eid, attrs in outputs.items():
            data[eid] = {}
            if self.current_transition is not None:
                for attr in attrs:
                    if self.current_transition['time'] == str(self.time):
                        if attr == transition_char_field:
                            data[eid][attr] = self.current_transition['input']
                        if attr == transition_time_field:
                            data[eid][attr] = self.current_transition['time']
                
        if self.current_transition['time'] == str(self.time):
            self.current_transition = None
        
        return data

    def step(self, time, inputs, max_advance):
        self.time = time
        print('controller time: ' + str(time))
        if self.current_transition is None or self.current_transition['time'] == str(time):
            next_transition = None

            if len(self.transition_chain) > 0:
                next_transition = self.transition_chain.pop(0)
                self.current_transition = next_transition
                return int(next_transition['time'])

if __name__ == '__main__':
    mosaik_api.start_simulation(CosimControllerAPI())