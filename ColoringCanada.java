
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ColoringCanada {
    // storing the provinces along with the neighbors in a hash map
    static Map<String, Set<String>> provineighbor = new HashMap<>();
    // storing province along with correct colour for it in a hash map
    static Map<String, String> provinceColor = new HashMap<>();
    // set of uncolored provinces
    static Set<String> nowNotColored;

    public void initializeNeighbors() {
        // giving the values of key, and values using the method put of Hash map
        provineighbor.put("NT", new HashSet<>(Arrays.asList("YU", "BC", "AL", "SA")));
        provineighbor.put("SA", new HashSet<>(Arrays.asList("NT", "AL", "MA")));
        provineighbor.put("NU", new HashSet<>(Arrays.asList("MA", "SA", "NT")));
        provineighbor.put("QU", new HashSet<>(Arrays.asList("ON", "NL", "NB")));
        provineighbor.put("BC", new HashSet<>(Arrays.asList("YU", "AL", "NT")));
        provineighbor.put("MA", new HashSet<>(Arrays.asList("SA", "NU", "ON")));
        provineighbor.put("AL", new HashSet<>(Arrays.asList("BC", "SA", "NT")));
        provineighbor.put("YU", new HashSet<>(Arrays.asList("NT", "BC")));
        provineighbor.put("ON", new HashSet<>(Arrays.asList("MA", "QU")));
        provineighbor.put("NB", new HashSet<>(Arrays.asList("QU", "NS")));
        provineighbor.put("NS", new HashSet<>(Arrays.asList("NB")));
        provineighbor.put("NL", new HashSet<>(Arrays.asList("QU")));

    }
    public static boolean backtracking(int availableColor) {
        // all provinces are coloured here
        if (nowNotColored.isEmpty()) {
            return true;
        }
        String canadaProvince = getMCVProvince();
        if(canadaProvince == null){
            return false;
        }
        List<String> leastConstraintValues = leastConstraint(canadaProvince, availableColor);
        for (String colorValue : leastConstraintValues) {
            // checking if the colour is valid and not same as neighbor colour
            if (correctColor(canadaProvince, colorValue)) {
                provinceColor.put(canadaProvince, colorValue);
                nowNotColored.remove(canadaProvince);
                if (backtracking(availableColor)) {
                    return true;
                }
                provinceColor.remove(canadaProvince);
                nowNotColored.add(canadaProvince);
            }
        }
        return false;
    }

    // creating an interface for the user to interact, take input of the number of colors and do the backtracking process.
    public static void userInterface() {
        JFrame mainFrame = new JFrame("Simple Map Colouring Problem");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // for giving information to user what it is about
        JOptionPane.showMessageDialog(mainFrame,
                "Showing you adjacent provinces with different colours for Canada");
       JPanel h = new JPanel();
       // number of colours user want to use for colouring
        JLabel question = new JLabel("<html><font size='5' color ='blue'>How many colors do you wish to colour the map in ?</font></html>");
        h.add(question);
        JComboBox<Integer>  colorInput = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        h.add(colorInput);
        h.setLayout(new FlowLayout(FlowLayout.CENTER));
        // showing output according to the number of colours wanted
        JTextArea showOutput = new JTextArea(30, 30);
        JScrollPane scrollPane = new JScrollPane(showOutput);
        mainFrame.add(h, BorderLayout.NORTH);
        mainFrame.add(scrollPane);
        mainFrame.setSize(800, 500);
        mainFrame.setVisible(true);
        colorInput.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int availableColor = (int) colorInput.getSelectedItem();
                showOutput.setText("");
                // clearing the hashmap provinceColor as the new colour is being selected
                provinceColor.clear();
                nowNotColored = new HashSet<>(provineighbor.keySet());
                // according to the result giving output to the user
                boolean result = backtracking(availableColor);
                if(result) {
                    for (Map.Entry<String, String> entry : provinceColor.entrySet()) {
                        showOutput.append(entry.getKey() + ": " + entry.getValue() + "\n");
                    }
                }
                else {
                    showOutput.append("Sorry, but cannot colour using "+ availableColor);
                }
            }
        });


    }
// giving the province and calculating the colours possible
    public static List<String> possibleColors(String canadaProvince, int availableColor) {
        List<String> possibleColors = new ArrayList<>();
        Set<String> colorsUsed = new HashSet<>();
        // in the colorsUsed put the colours that have been already given to neigboring province
        for (String eachNeighbor : provineighbor.get(canadaProvince)) {
            if (provinceColor.containsKey(eachNeighbor)) {
                colorsUsed.add(provinceColor.get(eachNeighbor));
            }
        }
        // colors that would be present
        String[] colors;
        colors = new String[]{"Purple", "green", "cyan", "yellow", "red"};
        int a = colors.length;
        // adding only those colors to the available colors that have not been used by neighboring provinces
        for (int k=0; k<availableColor; k++) {
            if (k < a) {
                possibleColors.add(colors[k]);
            }else{
                possibleColors.add("Color"+ (k-colors.length+1));
            }
        }
        possibleColors.removeAll(colorsUsed);
        return possibleColors;
    }


    // checking that neighbors have different colours
    public static boolean correctColor(String canadaProvince, String colorValue) {

        for (String neighbor: provineighbor.get(canadaProvince)) {

            if (provinceColor.containsKey(neighbor) && provinceColor.get(neighbor).equals(colorValue)) {
                return false;
            }

        }
        return true;
    }


    // finding the most constrainted variable
    public static String getMCVProvince() {
        String provinceMostConstraint = null;
        int remainedValue = Integer.MAX_VALUE;
        // looping through all the uncolored provinces to find the province that most neighbors (constraints)
        for (String eachProvince : nowNotColored) {
            int remainedValues = provineighbor.get(eachProvince).size();
            if (remainedValues < remainedValue) {
                remainedValue = remainedValues;
                provinceMostConstraint = eachProvince;
            }
        }
        return provinceMostConstraint;
    }



    // get Least Constraining values
    public static List<String> leastConstraint(String canadaProvince, int availableColor) {
        List<String> leastConstraintValues = new ArrayList<>();
        for (String eachColor : possibleColors(canadaProvince, availableColor)) {
            int constraintNumber = 0;
            // using foreach loop to go through the neighboring provinces that are coloured and looking for their possible colors
            for (String eachNeighbor : provineighbor.get(canadaProvince)) {
                if (nowNotColored.contains(eachNeighbor)) {
                    for (String colorOfNeighbor : possibleColors(eachNeighbor, availableColor)) {
                        if (colorOfNeighbor.equals(eachColor)) {
                            constraintNumber++;
                            break;
                        }
                    }
                }
            }
            // Storing the values in accordance to the constrainting values that is to find least constraining
            leastConstraintValues.add(eachColor);
        }
            // sorting
        leastConstraintValues.sort(Comparator.comparingInt(color -> countingCon(canadaProvince, color, availableColor)));


        return leastConstraintValues;
    }


// finding number of conflicts for a particulaar province with a particular colour
    public static int countingCon(String province, String color, int numColors) {
        int conflictNum = 0;
        for (String neighbor : provineighbor.get(province)) {
            if (nowNotColored.contains(neighbor)) {
                for (String neighborColor : possibleColors(neighbor, numColors)) {
                    if (neighborColor.equals(color)) {
                        conflictNum++;
                    }
                }
            }
        }
        System.out.print(conflictNum);
        return conflictNum;

    }


    public static void main(String[] args) {
        ColoringCanada e = new ColoringCanada();
        e.initializeNeighbors();
        nowNotColored = new HashSet<>(provineighbor.keySet());
        SwingUtilities.invokeLater(() -> userInterface());
        System.out.println("Yukon – YU, British Columbia-BC , Northwest Territories – NT,	Nunavut – NU ,Alberta – AL, Saskatchewan – SA , Manitoba – MA , Ontario – ON, Quebec – QU ,Newfoundland and Labrador – NL, New Brunswick – NB, Nova Scotia – NS");

    }
}


