package hwswcosim.swsim;

import tel.schich.automata.DFA;

/**
 * This class, along with a given {@link BinaryMapParser}, is to be used to
 * parse {@link DFAWrapper} instances from files that contain the necessary information
 * ({@link #parseDFAWrapper(String, String, String)}).
 */
public class DFAWrapperParser {
    private BinaryMapParser binaryMapParser;

    public DFAWrapperParser(BinaryMapParser binaryMapParser) {
        this.binaryMapParser = binaryMapParser;
    }

    public BinaryMapParser getBinaryMapParser() {
        return this.binaryMapParser;
    }

    /**
     * Parses a {@link DFAWrapper} with the information residing inside the given files.
     * 
     * @param resourceFolderPath The absolute path to the folder, in which resource files reside
     * @param DFAFileName The name of the file that describes the DFA
     * @param binaryMapFileName The name of the file that contains all {@link BinaryMapEntry} information
     * @return The said {@link DFAWrapper}
     */
    public DFAWrapper parseDFAWrapper(String resourceFolderPath, String DFAFileName, String binaryMapFileName) {
        DFA dfa = this.getBinaryMapParser().getDFAParser().parseDFA(resourceFolderPath, DFAFileName);

        return new DFAWrapper(
            dfa, 
            this.getBinaryMapParser().parseBinaryMap(dfa.getStates(), dfa.getTransitions(), resourceFolderPath, binaryMapFileName));
    }
}
