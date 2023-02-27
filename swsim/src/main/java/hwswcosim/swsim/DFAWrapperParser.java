package hwswcosim.swsim;

import tel.schich.automata.DFA;

public class DFAWrapperParser {
    private BinaryMapParser binaryMapParser;

    public DFAWrapperParser(BinaryMapParser binaryMapParser) {
        this.binaryMapParser = binaryMapParser;
    }

    public BinaryMapParser getBinaryMapParser() {
        return this.binaryMapParser;
    }

    public DFAWrapper parseDFAWrapper(String resourceFolderPath, String DFAFilePath, String binaryMapFilePath) {
        DFA dfa = this.getBinaryMapParser().getDFAParser().parseDFA(resourceFolderPath, DFAFilePath);

        return new DFAWrapper(
            dfa, 
            this.getBinaryMapParser().parseBinaryMap(dfa.getStates(), dfa.getTransitions(), resourceFolderPath, binaryMapFilePath));
    }
}
