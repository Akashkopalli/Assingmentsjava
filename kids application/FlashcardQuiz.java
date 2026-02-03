import java.awt.*;
import java.io.*;
import java.net.URI;
import java.util.*;
import java.util.List;
import javax.swing.*;

// Flashcard class
class Flashcard implements Serializable {
    String letter;
    String word;
    String imagePath;
    Flashcard next;

    public Flashcard(String letter, String word, String imagePath) {
        this.letter = letter;
        this.word = word;
        this.imagePath = imagePath;
        this.next = null;
    }
}

// Singly Linked List for flashcards
class FlashcardList implements Serializable {
    Flashcard head;
    int size = 0;

    public void add(String letter, String word, String imagePath) {
        Flashcard newCard = new Flashcard(letter, word, imagePath);
        if (head == null) {
            head = newCard;
        } else {
            Flashcard curr = head;
            while (curr.next != null) curr = curr.next;
            curr.next = newCard;
        }
        size++;
    }

    public void shuffle() {
        if (size < 2) return;
        java.util.List<Flashcard> list = toList();
        for (int i = list.size() - 1; i > 0; i--) {
            int j = new Random().nextInt(i + 1);
            Flashcard temp = list.get(i);
            list.set(i, list.get(j));
            list.set(j, temp);
        }
        // Rebuild linked list
        head = null;
        for (Flashcard card : list) {
            add(card.letter, card.word, card.imagePath);
        }
    }

    public void clear() {
        head = null;
        size = 0;
    }

    public java.util.List<Flashcard> toList() {
        java.util.List<Flashcard> list = new ArrayList<>();
        Flashcard curr = head;
        while (curr != null) {
            list.add(curr);
            curr = curr.next;
        }
        return list;
    }
}

public class FlashcardQuiz extends JFrame {
    // Use TreeMap for ordered storage of flashcards (DSA)
    private TreeMap<String, Flashcard> flashcardsMap = new TreeMap<>(); // Renamed to avoid conflict with FlashcardList instance
    private JTabbedPane tabbedPane; // Made instance variable for access from index page
    private TreeMap<String, Integer> tabNavigation = new TreeMap<>(); // DSA: Map for tab indices

    Stack<Flashcard> history;
    List<Flashcard> cardOrder; // For shuffle
    int currentIndex = 0;
    boolean shuffleMode = false;
    boolean repeatMode = false;
    boolean infiniteMode = false;

    // Flashcard tab components
    JLabel letterLabel, wordLabel, imageLabel;
    JButton nextBtn, prevBtn, shuffleBtn, repeatBtn, skipBtn, infiniteBtn, videoBtn;

    // Video tab data
    String[][] videos = {
        {"Learn ABCs", "https://www.youtube.com/watch?v=75p-N9YKqNo"},
        {"Phonics Song", "https://www.youtube.com/watch?v=BELlZKpi1Zs"},
        {"Alphabet Chant", "https://www.youtube.com/watch?v=36IBDpTRVNE"}
    };

    // Quiz tab components
    JLabel quizLetterLabel, quizImageLabel, quizFeedbackLabel;
    JButton[] quizOptionBtns = new JButton[4];
    int quizCurrentIndex = 0;
    int quizCorrectOption = 0;
    List<Flashcard> quizOrder;
    Random rand = new Random();
    private static final String ALPHABET_VIDEO_URL = "https://www.youtube.com/watch?v=75p-N9YKqNo";

    // Word bank for each letter
    private static final Map<String, List<String>> WORD_BANK = new HashMap<>();
    static {
        WORD_BANK.put("A", Arrays.asList("Apple", "Arrow", "Ant", "Apron", "Almond", "Anchor", "Album"));
        WORD_BANK.put("B", Arrays.asList("Ball", "Bear", "Book", "Bird", "Banana", "Button", "Basket"));
        WORD_BANK.put("C", Arrays.asList("Cat", "Cake", "Car", "Candy", "Chair", "Cloud", "Cactus"));
        WORD_BANK.put("D", Arrays.asList("Dog", "Door", "Duck", "Dress", "Dolphin", "Dragon", "Desert"));
        WORD_BANK.put("E", Arrays.asList("Egg", "Elephant", "Eagle", "Envelope", "Engine", "Elbow"));
        WORD_BANK.put("F", Arrays.asList("Fish", "Flower", "Flag", "Forest", "Frog", "Fruit", "Feather"));
        WORD_BANK.put("G", Arrays.asList("Goat", "Grape", "Grass", "Guitar", "Glove", "Giraffe", "Garden"));
        WORD_BANK.put("H", Arrays.asList("Hat", "House", "Horse", "Heart", "Honey", "Hammer", "Harbor"));
        WORD_BANK.put("I", Arrays.asList("Ice", "Igloo", "Insect", "Island", "Iron", "Ivory"));
        WORD_BANK.put("J", Arrays.asList("Jar", "Jelly", "Jeans", "Jewel", "Jungle", "Jacket"));
        WORD_BANK.put("K", Arrays.asList("Kite", "Key", "King", "Kitten", "Kiwi", "Kitchen"));
        WORD_BANK.put("L", Arrays.asList("Lion", "Leaf", "Lamp", "Lemon", "Ladder", "Lizard", "Letter"));
        WORD_BANK.put("M", Arrays.asList("Moon", "Mouse", "Milk", "Mirror", "Mountain", "Monkey"));
        WORD_BANK.put("N", Arrays.asList("Nest", "Nose", "Nut", "Night", "Noodle", "Navy"));
        WORD_BANK.put("O", Arrays.asList("Owl", "Orange", "Ocean", "Onion", "Otter", "Olive"));
        WORD_BANK.put("P", Arrays.asList("Pen", "Pig", "Pear", "Piano", "Pocket", "Parrot", "Pencil"));
        WORD_BANK.put("Q", Arrays.asList("Queen", "Quilt", "Quiz", "Quail", "Quarter"));
        WORD_BANK.put("R", Arrays.asList("Rose", "Ring", "Rabbit", "River", "Rocket", "Rainbow"));
        WORD_BANK.put("S", Arrays.asList("Sun", "Star", "Snake", "Spoon", "Snow", "Ship", "Sheep"));
        WORD_BANK.put("T", Arrays.asList("Tree", "Tiger", "Table", "Train", "Tomato", "Turtle"));
        WORD_BANK.put("U", Arrays.asList("Umbrella", "Unicorn", "Unit", "Uniform", "Uncle"));
        WORD_BANK.put("V", Arrays.asList("Van", "Vase", "Violin", "Village", "Volcano"));
        WORD_BANK.put("W", Arrays.asList("Wolf", "Water", "Window", "Whale", "Wheel", "Winter"));
        WORD_BANK.put("X", Arrays.asList("Xray", "Xylophone"));
        WORD_BANK.put("Y", Arrays.asList("Yarn", "Yogurt", "Yacht", "Yellow", "Yeti"));
        WORD_BANK.put("Z", Arrays.asList("Zebra", "Zero", "Zipper", "Zombie"));
    }
    // Store used words persistently
    private static final String USED_WORDS_FILE = "used_words.ser";
    private Map<String, Set<String>> usedWords = new HashMap<>();

    public FlashcardQuiz() {
        setTitle("Alphabet Learning Center");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(255, 245, 235));

        // Data structures initialization
        history = new Stack<>();

        // Load used words from file
        loadUsedWords();

        // Generate flashcards with random, unused words and store in TreeMap
        for (int i = 0; i < 26; i++) {
            String letter = String.valueOf((char)('A' + i));
            String word = generateRandomWord(letter);
            String imagePath = "images/" + letter + ".png";
            flashcardsMap.put(letter, new Flashcard(letter, word, imagePath)); // Use TreeMap
            usedWords.computeIfAbsent(letter, k -> new HashSet<>()).add(word);
        }
        saveUsedWords();

        cardOrder = new ArrayList<>(flashcardsMap.values()); // Convert TreeMap values to List for iteration
        currentIndex = 0;
        quizOrder = new ArrayList<>(cardOrder);
        Collections.shuffle(quizOrder);

        // Initialize tab navigation map (DSA)
        tabNavigation.put("Flashcards", 0);
        tabNavigation.put("Videos", 1);
        tabNavigation.put("Quiz", 2);
        tabNavigation.put("Dashboard", 3); // Add Dashboard tab index

        // Create the JTabbedPane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Comic Sans MS", Font.BOLD, 28));
        tabbedPane.addTab("Flashcards", createFlashcardPanel());
        tabbedPane.addTab("Videos", createVideoPanel());
        tabbedPane.addTab("Quiz", createQuizPanel());
        tabbedPane.addTab("Dashboard", createDashboardPanel()); // Add DSA-enhanced Dashboard

        // Initially show the Index Page
        add(createIndexPanel(), BorderLayout.CENTER);
    }

    // --- Index Page ---
    private JPanel createIndexPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.add(Box.createVerticalGlue()); // Push content to center

        JLabel welcomeTitle = new JLabel("Welcome to Funiversity!"); // Changed title
        welcomeTitle.setFont(new Font("Comic Sans MS", Font.BOLD, 72));
        welcomeTitle.setForeground(new Color(50, 150, 250));
        welcomeTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(welcomeTitle);
        panel.add(Box.createVerticalStrut(50));

        JLabel subtitle = new JLabel("Choose your learning adventure:");
        subtitle.setFont(new Font("Comic Sans MS", Font.PLAIN, 36));
        subtitle.setForeground(new Color(80, 80, 80));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(60));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 30));
        buttonPanel.setOpaque(false);

        Font btnFont = new Font("Comic Sans MS", Font.BOLD, 36);
        Dimension btnSize = new Dimension(350, 90);
        Color btnBgColor = new Color(200, 230, 255); // Light blue

        JButton flashcardsBtn = new JButton("Start Flashcards");
        flashcardsBtn.setFont(btnFont);
        flashcardsBtn.setBackground(btnBgColor);
        flashcardsBtn.setPreferredSize(btnSize);
        flashcardsBtn.addActionListener(e -> showMainContent(tabbedPane, "Flashcards"));
        buttonPanel.add(flashcardsBtn);

        JButton videosBtn = new JButton("Watch Videos");
        videosBtn.setFont(btnFont);
        videosBtn.setBackground(btnBgColor);
        videosBtn.setPreferredSize(btnSize);
        videosBtn.addActionListener(e -> showMainContent(tabbedPane, "Videos"));
        buttonPanel.add(videosBtn);

        JButton quizBtn = new JButton("Take Quiz");
        quizBtn.setFont(btnFont);
        quizBtn.setBackground(btnBgColor);
        quizBtn.setPreferredSize(btnSize);
        quizBtn.addActionListener(e -> showMainContent(tabbedPane, "Quiz"));
        buttonPanel.add(quizBtn);

        panel.add(buttonPanel);
        panel.add(Box.createVerticalGlue()); // Push content to center

        return panel;
    }

    private void showMainContent(JTabbedPane tabs, String tabName) {
        getContentPane().removeAll(); // Remove the index panel
        getContentPane().add(tabs, BorderLayout.CENTER); // Add the tabbed pane
        navigateToTab(tabName); // Navigate to the chosen tab
        revalidate(); // Re-layout the components
        repaint(); // Repaint the frame
    }

    private String generateRandomWord(String letter) {
        List<String> availableWords = new ArrayList<>(WORD_BANK.getOrDefault(letter, Collections.emptyList()));
        Set<String> used = usedWords.getOrDefault(letter, Collections.emptySet());
        availableWords.removeAll(used);
        if (availableWords.isEmpty()) {
            usedWords.put(letter, new HashSet<>());
            availableWords = new ArrayList<>(WORD_BANK.getOrDefault(letter, Collections.emptyList()));
        }
        return availableWords.isEmpty() ? letter.toLowerCase() : availableWords.get(rand.nextInt(availableWords.size()));
    }

    @SuppressWarnings("unchecked")
    private void loadUsedWords() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USED_WORDS_FILE))) {
            usedWords = (Map<String, Set<String>>) ois.readObject();
        } catch (FileNotFoundException e) {
            usedWords = new HashMap<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading used words: " + e.getMessage());
            usedWords = new HashMap<>();
        }
    }

    private void saveUsedWords() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USED_WORDS_FILE))) {
            oos.writeObject(usedWords);
        } catch (IOException e) {
            System.err.println("Error saving used words: " + e.getMessage());
        }
    }

    private JPanel createFlashcardPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setOpaque(false);
        letterLabel = new JLabel();
        letterLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 180));
        letterLabel.setForeground(new Color(0, 102, 204));
        letterLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imageLabel = new JLabel();
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        wordLabel = new JLabel();
        wordLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 60));
        wordLabel.setForeground(new Color(255, 102, 0));
        wordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(Box.createVerticalStrut(40));
        cardPanel.add(letterLabel);
        cardPanel.add(Box.createVerticalStrut(20));
        cardPanel.add(imageLabel);
        cardPanel.add(Box.createVerticalStrut(20));
        cardPanel.add(wordLabel);
        cardPanel.add(Box.createVerticalGlue());
        mainPanel.add(cardPanel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 20));
        prevBtn = new JButton();
        nextBtn = new JButton();
        shuffleBtn = new JButton();
        repeatBtn = new JButton();
        skipBtn = new JButton();
        infiniteBtn = new JButton();
        videoBtn = new JButton();
        Font btnFont = new Font("Comic Sans MS", Font.BOLD, 32);
        JButton[] btns = {prevBtn, nextBtn, shuffleBtn, repeatBtn, skipBtn, infiniteBtn, videoBtn};
        for (JButton btn : btns) {
            btn.setFont(btnFont);
            btn.setBackground(new Color(255, 255, 204));
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(280, 70));
        }
        setButtonIconsAndTooltips();
        buttonPanel.add(prevBtn);
        buttonPanel.add(nextBtn);
        buttonPanel.add(shuffleBtn);
        buttonPanel.add(repeatBtn);
        buttonPanel.add(skipBtn);
        buttonPanel.add(infiniteBtn);
        buttonPanel.add(videoBtn);
        prevBtn.addActionListener(e -> prevCard());
        nextBtn.addActionListener(e -> nextCard());
        shuffleBtn.addActionListener(e -> toggleShuffle());
        repeatBtn.addActionListener(e -> toggleRepeat());
        skipBtn.addActionListener(e -> skipCard());
        infiniteBtn.addActionListener(e -> toggleInfiniteMode());
        videoBtn.addActionListener(e -> openVideo(ALPHABET_VIDEO_URL));
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        updateCardDisplay();
        return mainPanel;
    }

    private void setButtonIconsAndTooltips() {
        prevBtn.setIcon(new ImageIcon("icons/back.png"));
        prevBtn.setText("Back");
        prevBtn.setToolTipText("Go to previous card");
        nextBtn.setIcon(new ImageIcon("icons/next.png"));
        nextBtn.setText("Next");
        nextBtn.setToolTipText("Go to next card");
        shuffleBtn.setIcon(new ImageIcon(shuffleMode ? "icons/shuffle_on.png" : "icons/shuffle_off.png"));
        shuffleBtn.setText("Shuffle");
        shuffleBtn.setToolTipText(shuffleMode ? "Shuffle is ON" : "Shuffle is OFF");
        repeatBtn.setIcon(new ImageIcon(repeatMode ? "icons/repeat_on.png" : "icons/repeat_off.png"));
        repeatBtn.setText("Repeat");
        repeatBtn.setToolTipText(repeatMode ? "Repeat is ON" : "Repeat is OFF");
        skipBtn.setIcon(new ImageIcon("icons/skip.png"));
        skipBtn.setText("Skip");
        skipBtn.setToolTipText("Skip to next card");
        infiniteBtn.setIcon(new ImageIcon(infiniteMode ? "icons/infinite_on.png" : "icons/infinite_off.png"));
        infiniteBtn.setText("Infinite");
        infiniteBtn.setToolTipText(infiniteMode ? "Infinite Mode is ON" : "Infinite Mode is OFF");
        videoBtn.setIcon(new ImageIcon("icons/video.png"));
        videoBtn.setText("Alphabet Video");
        videoBtn.setToolTipText("Watch the Alphabet Song");
    }

    private void updateCardDisplay() {
        if (cardOrder.isEmpty() || currentIndex < 0 || currentIndex >= cardOrder.size()) {
            letterLabel.setText("");
            wordLabel.setText("No cards!");
            imageLabel.setIcon(null);
            return;
        }
        Flashcard current = cardOrder.get(currentIndex);
        letterLabel.setText(current.letter);
        wordLabel.setText(current.letter + " for " + current.word);
        ImageIcon icon = new ImageIcon(current.imagePath);
        if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
            Image img = icon.getImage().getScaledInstance(320, 320, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(img));
        } else {
            imageLabel.setIcon(null);
        }
        setButtonIconsAndTooltips();
    }

    private void nextCard() {
        if (cardOrder.isEmpty()) return;
        history.push(cardOrder.get(currentIndex));
        if (currentIndex < cardOrder.size() - 1) {
            currentIndex++;
        } else if (repeatMode) {
            currentIndex = 0;
        } else if (infiniteMode) {
            currentIndex = 0;
        }
        updateCardDisplay();
    }

    private void prevCard() {
        if (!history.isEmpty()) {
            Flashcard prev = history.pop();
            currentIndex = cardOrder.indexOf(prev);
            updateCardDisplay();
        }
    }

    private void skipCard() {
        nextCard();
    }

    private void toggleShuffle() {
        shuffleMode = !shuffleMode;
        if (shuffleMode) {
            // Re-create cardOrder from TreeMap values to ensure consistent initial order before shuffle
            cardOrder = new ArrayList<>(flashcardsMap.values());
            Collections.shuffle(cardOrder);
            currentIndex = 0;
        } else {
            // Reset to original sorted order from TreeMap
            cardOrder = new ArrayList<>(flashcardsMap.values());
            currentIndex = 0;
        }
        history.clear();
        updateCardDisplay();
    }

    private void toggleRepeat() {
        repeatMode = !repeatMode;
        updateCardDisplay();
    }

    private void toggleInfiniteMode() {
        infiniteMode = !infiniteMode;
        updateCardDisplay();
    }

    private void openVideo(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Unable to open video link.");
        }
    }

    private JPanel createVideoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.add(Box.createVerticalStrut(30));
        JLabel title = new JLabel("YouTube Videos");
        title.setFont(new Font("Comic Sans MS", Font.BOLD, 60));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(30));
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);
        for (String[] video : videos) {
            JPanel videoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 10));
            videoPanel.setOpaque(false);
            JLabel videoTitle = new JLabel(video[0]);
            videoTitle.setFont(new Font("Comic Sans MS", Font.PLAIN, 36));
            JButton playBtn = new JButton("Play");
            playBtn.setFont(new Font("Comic Sans MS", Font.BOLD, 28));
            playBtn.setIcon(new ImageIcon("icons/video.png"));
            playBtn.addActionListener(e -> openVideo(video[1]));
            videoPanel.add(videoTitle);
            videoPanel.add(playBtn);
            listPanel.add(videoPanel);
        }
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        panel.add(scrollPane);
        return panel;
    }

    private JPanel createQuizPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.add(Box.createVerticalStrut(30));
        JLabel title = new JLabel("Quiz Time!");
        title.setFont(new Font("Comic Sans MS", Font.BOLD, 60));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(30));
        quizLetterLabel = new JLabel();
        quizLetterLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 160));
        quizLetterLabel.setForeground(new Color(0, 102, 204));
        quizLetterLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        quizImageLabel = new JLabel();
        quizImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(quizLetterLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(quizImageLabel);
        panel.add(Box.createVerticalStrut(20));
        JPanel optionsPanel = new JPanel();
        optionsPanel.setOpaque(false);
        optionsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 10));
        for (int i = 0; i < 4; i++) {
            quizOptionBtns[i] = new JButton();
            quizOptionBtns[i].setFont(new Font("Comic Sans MS", Font.BOLD, 40));
            quizOptionBtns[i].setPreferredSize(new Dimension(320, 80));
            int idx = i;
            quizOptionBtns[i].addActionListener(e -> checkQuizAnswer(idx));
            optionsPanel.add(quizOptionBtns[i]);
        }
        panel.add(optionsPanel);
        quizFeedbackLabel = new JLabel("");
        quizFeedbackLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 40));
        quizFeedbackLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(20));
        panel.add(quizFeedbackLabel);
        loadQuizQuestion();
        return panel;
    }

    private void loadQuizQuestion() {
        if (quizCurrentIndex >= quizOrder.size()) {
            quizFeedbackLabel.setText("Quiz Complete! Well done!");
            for (JButton btn : quizOptionBtns) btn.setEnabled(false);
            quizLetterLabel.setText("");
            quizImageLabel.setIcon(null);
            return;
        }
        Flashcard q = quizOrder.get(quizCurrentIndex);
        quizLetterLabel.setText(q.letter);
        ImageIcon icon = new ImageIcon(q.imagePath);
        if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
            Image img = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            quizImageLabel.setIcon(new ImageIcon(img));
        } else {
            quizImageLabel.setIcon(null);
        }
        List<String> options = new ArrayList<>();
        options.add(q.word);
        while (options.size() < 4) {
            String randomWord = cardOrder.get(rand.nextInt(cardOrder.size())).word;
            if (!options.contains(randomWord)) options.add(randomWord);
        }
        Collections.shuffle(options);
        quizCorrectOption = options.indexOf(q.word);
        for (int i = 0; i < 4; i++) {
            quizOptionBtns[i].setText(options.get(i));
            quizOptionBtns[i].setEnabled(true);
        }
        quizFeedbackLabel.setText("");
    }

    private void checkQuizAnswer(int selected) {
        for (JButton btn : quizOptionBtns) btn.setEnabled(false);
        if (selected == quizCorrectOption) {
            quizFeedbackLabel.setText("Correct! 🎉");
        } else {
            quizFeedbackLabel.setText("Oops! The answer is: " + quizOrder.get(quizCurrentIndex).word);
        }
        javax.swing.Timer timer = new javax.swing.Timer(1500, e -> {
            quizCurrentIndex++;
            loadQuizQuestion();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.add(Box.createVerticalStrut(30));
        
        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("Comic Sans MS", Font.BOLD, 60));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(30));
        
        // Navigation buttons with DSA
        JPanel buttonPanel = new JPanel();
        // Changed layout to BoxLayout.Y_AXIS for vertical stacking
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);
        
        // Add vertical strut between buttons for spacing
        Dimension buttonSpacing = new Dimension(0, 20); 

        JButton flashcardBtn = new JButton("Go to Flashcards");
        flashcardBtn.setFont(new Font("Comic Sans MS", Font.BOLD, 32));
        flashcardBtn.setBackground(new Color(255, 255, 204));
        flashcardBtn.setFocusPainted(false);
        flashcardBtn.setPreferredSize(new Dimension(320, 80)); // Consistent size
        flashcardBtn.setMaximumSize(new Dimension(320, 80)); // Ensure it doesn't expand horizontally
        flashcardBtn.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align in BoxLayout
        flashcardBtn.addActionListener(e -> navigateToTab("Flashcards"));
        
        JButton videoBtn = new JButton("Go to Videos");
        videoBtn.setFont(new Font("Comic Sans MS", Font.BOLD, 32));
        videoBtn.setBackground(new Color(255, 255, 204));
        videoBtn.setFocusPainted(false);
        videoBtn.setPreferredSize(new Dimension(320, 80)); // Consistent size
        videoBtn.setMaximumSize(new Dimension(320, 80)); // Ensure it doesn't expand horizontally
        videoBtn.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align in BoxLayout
        videoBtn.addActionListener(e -> navigateToTab("Videos"));
        
        JButton quizBtn = new JButton("Go to Quiz");
        quizBtn.setFont(new Font("Comic Sans MS", Font.BOLD, 32));
        quizBtn.setBackground(new Color(255, 255, 204));
        quizBtn.setFocusPainted(false);
        quizBtn.setPreferredSize(new Dimension(320, 80)); // Consistent size
        quizBtn.setMaximumSize(new Dimension(320, 80)); // Ensure it doesn't expand horizontally
        quizBtn.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align in BoxLayout
        quizBtn.addActionListener(e -> navigateToTab("Quiz"));
        
        buttonPanel.add(flashcardBtn);
        buttonPanel.add(Box.createRigidArea(buttonSpacing)); // Add space
        buttonPanel.add(videoBtn);
        buttonPanel.add(Box.createRigidArea(buttonSpacing)); // Add space
        buttonPanel.add(quizBtn);
        panel.add(buttonPanel);
        
        return panel;
    }
    
    private void navigateToTab(String tabName) {
        // Binary search on sorted tabNavigation keys (DSA)
        List<String> keys = new ArrayList<>(tabNavigation.keySet());
        int left = 0, right = keys.size() - 1;
        int targetIndex = -1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int cmp = keys.get(mid).compareTo(tabName);
            if (cmp == 0) {
                targetIndex = tabNavigation.get(keys.get(mid));
                break;
            } else if (cmp < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        if (targetIndex != -1) {
            tabbedPane.setSelectedIndex(targetIndex);
        } else {
            JOptionPane.showMessageDialog(this, "Tab not found!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlashcardQuiz quiz = new FlashcardQuiz();
            quiz.setVisible(true);
        });
    }
}