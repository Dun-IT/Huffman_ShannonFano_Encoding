import java.util.*;

class ShannonFanoNode {
    char character;
    int frequency;
    String code;

    public ShannonFanoNode(char character, int frequency) {
        this.character = character;
        this.frequency = frequency;
        this.code = "";
    }

    public int getFrequency() {
        return frequency;
    }
}

class HuffmanNode {
    char character;
    int frequency;
    HuffmanNode leftChild;
    HuffmanNode rightChild;

    public HuffmanNode(char character, int frequency) {
        this.character = character;
        this.frequency = frequency;
    }

    public boolean isLeaf() {
        return leftChild == null && rightChild == null;
    }
}

public class Encoding {

    /**
     * MAIN FUNCTION
     **/
    public static void main(String[] args) {
        // Nhap chuoi tu nguoi dung
        Scanner scanner = new Scanner(System.in);
        System.out.print("Nhap vao mot chuoi: ");
        String input = scanner.nextLine();
        scanner.close();

        // Build ma
        Map<Character, String> huffmanCodes = buildHuffmanCodes(input);
        Map<Character, String> shannonFanoCodes = buildShannonFanoCodes(input);

        // entrySet() method: Tao ra 1 tap hop chua cac phan tu trong HashMap
        System.out.println("Ma hoa Huffman:");
        for (Map.Entry<Character, String> entry : huffmanCodes.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        // entrySet() method: Tao ra 1 tap hop chua cac phan tu trong HashMap
        System.out.println("Ma hoa Shannon-Fano:");
        for (Map.Entry<Character, String> entry : shannonFanoCodes.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        String huffmanEncodedString = encodeString(input, huffmanCodes);
        System.out.println("Chuoi da ma hoa Huffman: " + huffmanEncodedString);

        String shannonFanoEncodedString = encodeString(input, shannonFanoCodes);
        System.out.println("Chuoi da ma hoa Shannon-Fano: " + shannonFanoEncodedString);

        double entropy = calculateEntropy(input);
        double averageCodeLength = calculateAverageCodeLength(input, shannonFanoCodes);
        double compressionEfficiency = entropy / averageCodeLength;
        double redundancy = 1 - compressionEfficiency;

        System.out.println("Hieu suat ma hoa: " + compressionEfficiency);
        System.out.println("Du thua: " + redundancy);
    }

    /**
     * buildHuffmanCodes FUNCTION: Tra ve 1 HashMap chua ky tu (key) va chuoi ma hoa (value)
     **/
    public static Map<Character, String> buildHuffmanCodes(String input) {
        // frequencyMap: HashMap luu cac ky tu va tan suat xuat hien
        // getOrDefault() method: Lay value tuong ung voi key neu da ton tai, neu chua ton tai gan bang defaultValue

        Map<Character, Integer> frequencyMap = new HashMap<>();
        for (char c : input.toCharArray()) {
            char lowercaseChar = Character.toLowerCase(c); // Chuyen chuoi ve ky tu thuong va dem
            frequencyMap.put(lowercaseChar, frequencyMap.getOrDefault(lowercaseChar, 0) + 1);
        }

        // List luu cac Node trong HashMap
        List<HuffmanNode> nodeList = new ArrayList<>();
        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            HuffmanNode node = new HuffmanNode(entry.getKey(), entry.getValue());
            nodeList.add(node);
        }

        buildHuffmanTree(nodeList);

        Map<Character, String> codes = new HashMap<>();
        generateHuffmanCodes(nodeList.get(0), "", codes);

        return codes;
    }

    /**
     * buildHuffmanTree FUNCTION: Tao cay tu các Node trong List
     */
    public static void buildHuffmanTree(List<HuffmanNode> nodeList) {
        while (nodeList.size() > 1) {
            // Sap xep list tu be den lon theo tan suat xuat hien
            nodeList.sort(Comparator.comparingInt(node -> node.frequency));

            // Tao node trai, node phai
            HuffmanNode leftChild = nodeList.get(0);
            HuffmanNode rightChild = nodeList.get(1);

            // Node cha bang tong tan suat cua node trai va phai
            HuffmanNode parent = new HuffmanNode('\0', leftChild.frequency + rightChild.frequency);
            parent.leftChild = leftChild;
            parent.rightChild = rightChild;

            // Xoa 2 node da xet
            nodeList.remove(0);
            nodeList.remove(0);

            // Them node cha sau khi tao
            nodeList.add(parent);
        }
    }

    /**
     * generateHuffmanCodes FUNCTION: Danh chi so trai 0, phai 1
     */
    public static void generateHuffmanCodes(HuffmanNode root, String currentCode, Map<Character, String> codes) {
        if (root == null) {
            return;
        }

        // Neu la nut la moi them toan bo chuoi ma hoa vao trong HashMao codes
        if (root.isLeaf()) {
            char lowercaseChar = Character.toLowerCase(root.character);
            codes.put(lowercaseChar, currentCode);
        }

        // Danh so theo thu tu pre-order
        generateHuffmanCodes(root.leftChild, currentCode + "0", codes);
        generateHuffmanCodes(root.rightChild, currentCode + "1", codes);
    }

    /**
     * buildShannonFanoCodes FUNCTION: Tra ve HashMap chua ky tu va chuoi ma hoa
     */
    public static Map<Character, String> buildShannonFanoCodes(String input) {
        Map<Character, Integer> frequencyMap = new HashMap<>();

        // Dem tan so va luu vao HashMap
        for (char c : input.toCharArray()) {
            char lowercaseChar = Character.toLowerCase(c);
            frequencyMap.put(lowercaseChar, frequencyMap.getOrDefault(lowercaseChar, 0) + 1);
        }

        // Luu cac phan tu trong HashMap vao mang
        List<ShannonFanoNode> nodeList = new ArrayList<>();
        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            char character = entry.getKey();
            int frequency = entry.getValue();
            ShannonFanoNode node = new ShannonFanoNode(character, frequency);
            nodeList.add(node);
        }

        buildShannonFanoTree(nodeList);

        // HashMap codes: Luu ky tu (key) va chuoi ma hoa (value)
        Map<Character, String> codes = new HashMap<>();
        assignShannonFanoCodes(nodeList, "", codes);

        return codes;
    }

    /**
     * buildShannonFanoTree FUNCTION: Dung cay
     */
    public static void buildShannonFanoTree(List<ShannonFanoNode> nodeList) {
        // Sap xep cac nut theo tan suat giam dan
        sortNodesByFrequency((nodeList));

        // Tao stack va them List vao trong stack, stack ban dau chi chua 1 List
        // push() method: Them cac phan tu vao dinh stack
        Stack<List<ShannonFanoNode>> stack = new Stack<>();
        stack.push(nodeList);

        while (!stack.isEmpty()) {
            // pop method(): Lay phan tu o dinh va xoa phan tu do ra khoi stack
            List<ShannonFanoNode> nodes = stack.pop();

            // Neu kich cu cua List duoc lay ra chi co 1 phan tu, bo qua phan chia theo tan suat
            if (nodes.size() <= 1) {
                continue;
            }

            // Tinh tong tan suat
            int sumFrequency = calculateTotalFrequency(nodes);

            // Tim phan tu de chia List
            int mid = findSplitIndex(nodes, sumFrequency);

            // subList(int fromIndex, int toIndex) method: tra ve List cac phan tu
            // List 1: Tu dau toi mid
            List<ShannonFanoNode> group1 = nodes.subList(0, mid);

            // List 2: Tu mid toi size cua List ban dau
            List<ShannonFanoNode> group2 = nodes.subList(mid, nodes.size());

            // Ma hoa cho cac phan tu co trong List 1 them 0
            for (ShannonFanoNode node : group1) {
                node.code += "1";
            }

            // Ma hoa cho cac phan tu co trong List 1 them 0
            for (ShannonFanoNode node : group2) {
                node.code += "0";
            }

            // Them 2 List vao stack de tiep tuc chia
            stack.push(group1);
            stack.push(group2);
        }
    }

    /**
     * calculateTotalFrequency FUNCTION: Tinh tong tan suat xuat hien trong 1 List
     */
    public static int calculateTotalFrequency(List<ShannonFanoNode> nodeList) {
        int sumFrequency = 0;
        for (ShannonFanoNode node : nodeList) {
            sumFrequency += node.frequency;
        }
        return sumFrequency;
    }

    /**
     * findSplitIndex FUNCTION: Tim phan tu chia List thanh 2 phan co frequency tuong duong nhau
     */
    public static int findSplitIndex(List<ShannonFanoNode> nodeList, int sumFrequency) {
        int cumulativeFrequency = 0;
        int mid = 0;

        // Gan cumulativeFrequency = 0, cong lan luot cac frequency va so sanh voi tong tan suat / 2
        for (int i = 0; i < nodeList.size(); i++) {
            ShannonFanoNode node = nodeList.get(i);
            cumulativeFrequency += node.frequency;

            // neu < sumFrequency -> Tim duoc vi tri tai i
            if (cumulativeFrequency <= sumFrequency / 2) {
                mid = i;
            } else {
                break;
            }
        }

        // subList(int fromIndex, int toIndex): List duoc tao toi toIndex - 1 nen mid phai + 1
        return mid + 1;
    }

    /**
     * assignShannonFanoCodes FUNCTION: Duyet cac ky tu co trong List, cap nhat chuoi ma hoa trong List, them vao HashMap
     */
    public static void assignShannonFanoCodes(List<ShannonFanoNode> nodeList, String code, Map<Character, String> codes) {
        for (ShannonFanoNode node : nodeList) {
            node.code = code + node.code;
            codes.put(node.character, node.code);
        }
    }

    /**
     * encodeString FUNCTION: Chuyen toan bo chuoi duoc nhap thanh chuoi duoc ma hoa
     */
    public static String encodeString(String input, Map<Character, String> codes) {
        StringBuilder encodedString = new StringBuilder();

        for (char c : input.toCharArray()) {
            // Chuoi input chua ca ky tu hoa va thuong nhung trong HashMap chi luu thuong nen phai lowerCase
            char lowercaseChar = Character.toLowerCase(c);
            String code = codes.get(lowercaseChar); // get() method: Lay value voi key tuong ung trong HashMap
            if (code != null) {
                encodedString.append(code);
            }
        }

        return encodedString.toString();
    }

    /**
     * calculateEntropy FUNCTION: Tinh entropy
     * Entropy = - ∑ (p_i * log2(p_i))
     */
    public static double calculateEntropy(String input) {
        Map<Character, Integer> frequencyMap = new HashMap<>();

        // Dem tuan suat xuat hien cua cac ky tu
        for (char c : input.toCharArray()) {
            char lowercaseChar = Character.toLowerCase(c);
            frequencyMap.put(lowercaseChar, frequencyMap.getOrDefault(lowercaseChar, 0) + 1);
        }

        int totalCharacters = input.length();
        double entropy = 0;

        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            // probability: Tinh xac suat tu HashMap
            double probability = (double) entry.getValue() / totalCharacters;
            entropy -= probability * (Math.log(probability) / Math.log(2));
        }

        return entropy;
    }

    /**
     * calculateAverageCodeLength FUNCTION: Tinh do dai ma hoa trung binh
     * Average Code Length = ∑ (p_i * l_i), p_i = so lan xuat hien / do dai chuoi
     */
    public static double calculateAverageCodeLength(String input, Map<Character, String> codes) {
        int totalCharacters = input.length();
        double averageCodeLength = 0;

        for (char c : input.toCharArray()) {
            char lowercaseChar = Character.toLowerCase(c);
            String code = codes.get(lowercaseChar); // Lay so lan duoc ma hoa * do dai chuoi ma hoa
            if (code != null) {
                averageCodeLength += code.length(); // Chia cho do dai chuoi
            }
        }

        return averageCodeLength / totalCharacters;
    }

    /**
     * sortNodesByFrequency FUNCTION: Sap xep noi bot tan suat 2 node theo thu tu giam dan
     */
    public static void sortNodesByFrequency(List<ShannonFanoNode> nodeList) {
        for (int i = 0; i < nodeList.size() - 1; i++) {
            for (int j = i + 1; j < nodeList.size(); j++) {
                ShannonFanoNode node1 = nodeList.get(i);
                ShannonFanoNode node2 = nodeList.get(j);

                if (node2.getFrequency() > node1.getFrequency()) {
                    // Hoan doi vi tri 2 node
                    Collections.swap(nodeList, i, j);
                }
            }
        }
    }
}