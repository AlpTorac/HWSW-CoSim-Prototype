import agent

class AgentManager():
    def __init__(self):
        self.agents = []
        self.processed_input = None
    
    def add_agent(self, *agents, **params):
        if agents is not None and len(agents) > 0:
            self.agents.extend(agents)
            
        if params is not None:
            self.agents.append(agent.Agent(**params))
        
    def process_input(self, **input):
        result = input
        
        for agent in self.agents:
            result = agent.process_input(**result)
            
        self.processed_input = result
    
    def get_processed_input(self):
        result = self.processed_input
        self.processed_input = None
        return result
    
    def get_transition_char(self, result):
        return 'b'