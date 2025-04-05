package fr.acth2.redcap.application.firstlog;

import fr.acth2.redcap.effects.MainBackgroundPanel;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

public class FirstLogFrame extends JFrame {
    private JLabel welcomeLabel;
    private RoundedButton nextButton;
    private MainBackgroundPanel backgroundPanel;
    private Point mouseDownCompCoords;
    private static final int CORNER_RADIUS = 20;
    private static final int BORDER_THICKNESS = 2;
    private Timer animationTimer;
    private Point targetLocation;
    private Point currentLocation;
    private static final int ANIMATION_DURATION = 300;
    private int activeTheme;
    private static final int THEME_SWITCH_INTERVAL = 10000;
    private static final int FIELD_HEIGHT = 50;
    private Timer themeSwitchTimer;
    private int[] themes = {1, 2, 3};
    private int currentThemeIndex = 0;
    private Timer buttonTransitionTimer;
    private float buttonTransitionProgress = 0f;
    private Color currentButtonBg;
    private Color targetButtonBg;
    private Color currentButtonBorder;
    private Color targetButtonBorder;
    private Color currentButtonHover;
    private Color currentButtonClick;
    private JPanel centerPanel;
    private JSeparator separator;
    private Timer separatorAnimationTimer;
    private int separatorTargetX;
    private static final int SEPARATOR_ANIMATION_DURATION = 500;
    private RoundedButton prevButton;
    private RoundedButton navNextButton;
    private JPanel categoriesPanel;
    private java.util.List<String> categories = new java.util.ArrayList<>();
    private int currentCategoryIndex = 0;
    private JPanel navButtonsPanel;
    private JPanel rightPanel;

    private boolean moveSeparatorOnce = true;

    private static final int FADE_DURATION = 300;
    private Timer fadeTimer;
    private float fadeAlpha = 1.0f;
    private JComponent currentFadeComponent;
    private boolean isFadingIn = false;
    private JSeparator leftSeparator;

    public FirstLogFrame(int starterTheme) {
        super("Introduction");

        for (int i = 0; i < themes.length; i++) {
            if (themes[i] == starterTheme) {
                currentThemeIndex = i;
                break;
            }
        }
        this.activeTheme = starterTheme;

        setUndecorated(true);
        initializeBackgroundPanel();
        createUIComponents();
        setupUI();
        applyRoundedCorners();

        categories.add("1");
        categories.add("2");
        categories.add("3");
        categories.add("4");

        setVisible(true);

        Timer initialDelayTimer = new Timer(500, e -> startThemeSwitcher());
        initialDelayTimer.setRepeats(false);
        initialDelayTimer.start();
    }

    private void initializeBackgroundPanel() {
        backgroundPanel = new MainBackgroundPanel(activeTheme) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();

                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int borderSize = BORDER_THICKNESS;
                RoundRectangle2D border = new RoundRectangle2D.Double(
                        borderSize/2, borderSize/2,
                        getWidth() - borderSize, getHeight() - borderSize,
                        CORNER_RADIUS, CORNER_RADIUS
                );

                Color borderStartColor = new Color(10, 40, 10);
                Color borderEndColor = new Color(10, 100, 10);

                switch (activeTheme) {
                    case 1:
                        borderStartColor = new Color(10, 10, 40);
                        borderEndColor = new Color(10, 10, 100);
                        break;
                    case 3:
                        borderStartColor = new Color(40, 10, 10);
                        borderEndColor = new Color(100, 10, 10);
                        break;
                }

                GradientPaint gradient = new GradientPaint(
                        0, 0, borderStartColor,
                        getWidth(), getHeight(), borderEndColor
                );

                g2d.setPaint(gradient);
                g2d.setStroke(new BasicStroke(borderSize));
                g2d.draw(border);
                g2d.dispose();
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);
    }

    private void createUIComponents() {
        switch(activeTheme) {
            case 1:
                currentButtonBg = new Color(10, 10, 30);
                currentButtonBorder = new Color(10, 10, 40);
                currentButtonHover = new Color(20, 20, 60);
                currentButtonClick = new Color(15, 15, 45);
                break;
            case 3:
                currentButtonBg = new Color(30, 10, 10);
                currentButtonBorder = new Color(40, 10, 10);
                currentButtonHover = new Color(60, 20, 20);
                currentButtonClick = new Color(45, 15, 15);
                break;
            default:
                currentButtonBg = new Color(10, 30, 10);
                currentButtonBorder = new Color(10, 40, 10);
                currentButtonHover = new Color(20, 60, 20);
                currentButtonClick = new Color(15, 45, 15);
        }
        targetButtonBg = currentButtonBg;
        targetButtonBorder = currentButtonBorder;

        welcomeLabel = new JLabel("Welcome to RedCap", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
                super.paintComponent(g2d);
                g2d.dispose();
            }
        };
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(60, 0, 0, 0));

        nextButton = new RoundedButton("NEXT");
        nextButton.setFont(new Font("Segoe UI", Font.BOLD, 22));
        nextButton.setPreferredSize(new Dimension(300, 80));
        nextButton.setForeground(Color.WHITE);
        nextButton.setFocusPainted(false);
    }

    private JPanel createWelcomePanel() {
        JPanel welcomePanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
                super.paintComponent(g2d);
                g2d.dispose();
            }
        };
        welcomePanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 50, 0);
        welcomePanel.add(welcomeLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(20, 0, 0, 0);
        welcomePanel.add(nextButton, gbc);

        return welcomePanel;
    }

    private void setupUI() {
        JPanel titleBar = createTitleBar();

        leftSeparator = new JSeparator(JSeparator.VERTICAL) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D)g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.fillRect(0, 0, 1, getHeight());
                g2d.dispose();
            }
        };
        leftSeparator.setPreferredSize(new Dimension(1, 700));

        centerPanel = new JPanel(new CardLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
                super.paintComponent(g2d);
                g2d.dispose();
            }
        };
        centerPanel.setOpaque(false);

        JPanel welcomePanel = createWelcomePanel();

        JPanel contentPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
                super.paintComponent(g2d);
                g2d.dispose();
            }
        };
        contentPanel.setOpaque(false);

        JPanel leftPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
                super.paintComponent(g2d);
                g2d.dispose();
            }
        };
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension((int)(900 * 0.25), 700));

        separator = new JSeparator(JSeparator.VERTICAL) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D)g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.fillRect(0, 0, 1, getHeight());
                g2d.dispose();
            }
        };
        separator.setPreferredSize(new Dimension(1, 700));

        rightPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
                super.paintComponent(g2d);
                g2d.dispose();
            }
        };
        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new Dimension((int)(900 * 0.75), 700));

        categoriesPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
                super.paintComponent(g2d);
                g2d.dispose();
            }
        };
        categoriesPanel.setOpaque(false);
        categoriesPanel.setLayout(new BoxLayout(categoriesPanel, BoxLayout.Y_AXIS));
        categoriesPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        for (String category : categories) {
            JLabel categoryLabel = new JLabel(category) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
                    super.paintComponent(g2d);
                    g2d.dispose();
                }
            };
            categoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            categoryLabel.setForeground(Color.WHITE);
            categoryLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            categoriesPanel.add(categoryLabel);
        }
        leftPanel.add(categoriesPanel, BorderLayout.CENTER);

        navButtonsPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
                super.paintComponent(g2d);
                g2d.dispose();
            }
        };
        navButtonsPanel.setOpaque(false);
        navButtonsPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        prevButton = new RoundedButton("PREV");
        prevButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        prevButton.setPreferredSize(new Dimension(120, 50));
        prevButton.setVisible(true);
        prevButton.addActionListener(e -> showPreviousCategory());

        navNextButton = new RoundedButton("NEXT");
        navNextButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        navNextButton.setPreferredSize(new Dimension(120, 50));
        navNextButton.setVisible(true);
        navNextButton.addActionListener(e -> {
            showNextCategory();
        });

        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
                super.paintComponent(g2d);
                g2d.dispose();
            }
        };
        buttonContainer.setOpaque(false);
        buttonContainer.add(prevButton);
        buttonContainer.add(navNextButton);

        navButtonsPanel.add(buttonContainer, BorderLayout.SOUTH);
        rightPanel.add(navButtonsPanel, BorderLayout.SOUTH);

        JPanel leftContainer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
                super.paintComponent(g2d);
                g2d.dispose();
            }
        };
        leftContainer.setOpaque(false);
        leftContainer.add(leftPanel, BorderLayout.CENTER);
        leftContainer.setPreferredSize(new Dimension((int)(900 * 0.25), 700));

        JPanel separatorContainer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
                super.paintComponent(g2d);
                g2d.dispose();
            }
        };
        separatorContainer.setOpaque(false);
        separatorContainer.add(separator, BorderLayout.CENTER);
        separatorContainer.setPreferredSize(new Dimension(1, 700));

        JPanel rightContainer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
                super.paintComponent(g2d);
                g2d.dispose();
            }
        };
        rightContainer.setOpaque(false);
        rightContainer.add(rightPanel, BorderLayout.CENTER);
        rightContainer.setPreferredSize(new Dimension((int)(900 * 0.75), 700));

        contentPanel.add(leftContainer, BorderLayout.WEST);
        contentPanel.add(separatorContainer, BorderLayout.CENTER);
        contentPanel.add(rightContainer, BorderLayout.EAST);

        centerPanel.add(welcomePanel, "welcome");
        centerPanel.add(contentPanel, "content");

        nextButton.addActionListener(e -> {
            separatorTargetX = (int)(getWidth() * 0.25);
            createLeftSeparator();
            ((CardLayout)centerPanel.getLayout()).show(centerPanel, "content");
            showCategory(0);
        });

        backgroundPanel.add(titleBar, BorderLayout.NORTH);
        backgroundPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setOpaque(false);
        mainContentPanel.add(leftSeparator, BorderLayout.WEST);
        mainContentPanel.add(centerPanel, BorderLayout.CENTER);

        backgroundPanel.add(titleBar, BorderLayout.NORTH);
        backgroundPanel.add(mainContentPanel, BorderLayout.CENTER);

        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void createLeftSeparator() {
        if (leftSeparator != null) {
            backgroundPanel.remove(leftSeparator);
        }

        leftSeparator = new JSeparator(JSeparator.VERTICAL) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D)g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.fillRect(0, 0, 1, getHeight());
                g2d.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(1, getParent().getHeight());
            }
        };
        leftSeparator.setBounds(moveSeparatorOnce ? (int) (getWidth() * 0.0) : (int) (getWidth() * 0.23), titleBar.getHeight(), 1, getHeight() - titleBar.getHeight());

        if (moveSeparatorOnce) {

            final int targetX = (int) (getWidth() * 0.23);
            Timer animationTimer = new Timer(16, new ActionListener() {
                private long startTime = -1;
                private final int duration = 1000;

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (startTime < 0) {
                        startTime = System.currentTimeMillis();
                    }

                    long elapsed = System.currentTimeMillis() - startTime;
                    float progress = Math.min(1.0f, (float) elapsed / duration);

                    progress--;
                    float easedProgress = progress * progress * progress + 1;

                    int currentX = (int) (-10 + (targetX + 10) * easedProgress);
                    leftSeparator.setLocation(currentX, titleBar.getHeight());

                    if (progress >= 1.0f) {
                        ((Timer) e.getSource()).stop();
                        leftSeparator.setLocation(targetX, titleBar.getHeight());
                    }
                }
            });
            moveSeparatorOnce = false;
            animationTimer.start();
        }
        backgroundPanel.setLayout(null);
        backgroundPanel.add(leftSeparator);
        backgroundPanel.revalidate();
        backgroundPanel.repaint();
    }


    private void showNextCategory() {
        if (currentCategoryIndex < categories.size() - 1) {
            currentCategoryIndex++;
            showCategory(currentCategoryIndex);
            if (currentCategoryIndex == categories.size() - 1) {
                navNextButton.setText("FINISH");
            }
        }
        else if (currentCategoryIndex == categories.size() - 1 && navNextButton.getText().equals("FINISH")) {
            System.out.println("All categories completed!");
        }
    }

    private void showPreviousCategory() {
        if (currentCategoryIndex > 0) {
            currentCategoryIndex--;
            showCategory(currentCategoryIndex);
            if (navNextButton.getText().equals("FINISH")) {
                navNextButton.setText("NEXT");
            }
        }
    }

    private void showCategory(int index) {
        for (Component comp : categoriesPanel.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                if (categories.get(currentCategoryIndex).equals(label.getText())) {
                    fadeOutComponent(label, () -> {
                        label.setForeground(Color.WHITE);
                        label.setFont(label.getFont().deriveFont(Font.PLAIN, 16));
                        fadeInComponent(label);
                    });
                }
            }
        }

        for (Component comp : categoriesPanel.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                if (categories.get(index).equals(label.getText())) {
                    fadeOutComponent(label, () -> {
                        label.setForeground(new Color(100, 255, 100));
                        label.setFont(label.getFont().deriveFont(Font.BOLD, 18));
                        fadeInComponent(label);
                    });
                }
            }
        }

        currentCategoryIndex = index;
        updateRightPanelContent(categories.get(index));
        updateNavButtons();
    }

    private void updateRightPanelContent(String category) {
        Component[] components = rightPanel.getComponents();
        for (Component comp : components) {
            if (comp != navButtonsPanel) {
                fadeOutComponent((JComponent) comp, () -> {
                    rightPanel.remove(comp);
                    JPanel newContent = createContentPanelForCategory(category);
                    fadeInComponent(newContent);
                    rightPanel.add(newContent, BorderLayout.CENTER);
                    rightPanel.revalidate();
                });
            }
        }

        if (components.length <= 1) {
            JPanel newContent = createContentPanelForCategory(category);
            fadeInComponent(newContent);
            rightPanel.add(newContent, BorderLayout.CENTER);
            rightPanel.revalidate();
        }
    }

    private JPanel createContentPanelForCategory(String category) {
        JPanel contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (fadeAlpha < 1.0f) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
                    super.paintComponent(g2d);
                    g2d.dispose();
                }
            }
        };
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));

        JLabel titleLabel = new JLabel(category) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
                super.paintComponent(g2d);
                g2d.dispose();
            }
        };
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(20));

        switch (category) {
            case "1":
                contentPanel.add(createFormField("1"));
                contentPanel.add(createFormField("2"));
                contentPanel.add(createFormField("3"));
                break;
            case "2":
                contentPanel.add(createFormField("1"));
                contentPanel.add(createPasswordField("2"));
                contentPanel.add(createPasswordField("3"));
                break;
            case "3":
                contentPanel.add(createComboBoxField("Theme", new String[]{"1", "2", "3"}));
                contentPanel.add(createComboBoxField("Language", new String[]{"English", "French"}));
                break;
            case "4":
                contentPanel.add(createComboBoxField("1", new String[]{"2", "3", "4"}));
                contentPanel.add(createFormField("5"));
                break;
        }

        return contentPanel;
    }

    private void fadeOutComponent(JComponent component, Runnable onComplete) {
        if (fadeTimer != null && fadeTimer.isRunning()) {
            fadeTimer.stop();
        }

        currentFadeComponent = component;
        isFadingIn = false;
        fadeAlpha = 1.0f;

        fadeTimer = new Timer(16, e -> {
            fadeAlpha -= 16f / FADE_DURATION;
            if (fadeAlpha <= 0) {
                fadeAlpha = 0;
                fadeTimer.stop();
                onComplete.run();
            }
            component.repaint();
        });
        fadeTimer.start();
    }

    private void fadeInComponent(JComponent component) {
        if (fadeTimer != null && fadeTimer.isRunning()) {
            fadeTimer.stop();
        }

        currentFadeComponent = component;
        isFadingIn = true;
        fadeAlpha = 0f;
        component.setVisible(true);

        fadeTimer = new Timer(16, e -> {
            fadeAlpha += 16f / FADE_DURATION;
            if (fadeAlpha >= 1) {
                fadeAlpha = 1;
                fadeTimer.stop();
            }
            component.repaint();
        });
        fadeTimer.start();
    }

    private void updateNavButtons() {
        prevButton.setEnabled(currentCategoryIndex > 0);
        navNextButton.setEnabled(currentCategoryIndex < categories.size() - 1);
        prevButton.repaint();
        navNextButton.repaint();
    }

    private JPanel createFormField(String labelText) {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
                super.paintComponent(g2d);
                g2d.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, FIELD_HEIGHT + 30));

        JLabel label = new JLabel(labelText) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
                super.paintComponent(g2d);
                g2d.dispose();
            }
        };
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(Color.WHITE);

        JTextField textField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
                g2.setColor(currentButtonBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);

                g2.setColor(currentButtonBorder);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, CORNER_RADIUS, CORNER_RADIUS);

                g2.setColor(getForeground());
                super.paintComponent(g2);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                d.height = FIELD_HEIGHT;
                return d;
            }
        };
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setForeground(Color.WHITE);
        textField.setOpaque(false);
        textField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        panel.add(label, BorderLayout.NORTH);
        panel.add(textField, BorderLayout.CENTER);
        panel.add(Box.createVerticalStrut(10), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createPasswordField(String labelText) {
        JPanel panel = createFormField(labelText);
        JPasswordField passwordField = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
                g2.setColor(currentButtonBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);

                g2.setColor(currentButtonBorder);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, CORNER_RADIUS, CORNER_RADIUS);

                g2.setColor(getForeground());
                super.paintComponent(g2);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                d.height = FIELD_HEIGHT;
                return d;
            }
        };

        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setForeground(Color.WHITE);
        passwordField.setOpaque(false);
        passwordField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        panel.remove(1);
        panel.add(passwordField, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createComboBoxField(String labelText, String[] options) {
        JPanel panel = createFormField(labelText);
        JComboBox<String> comboBox = new JComboBox(options) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
                g2.setColor(currentButtonBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);

                g2.setColor(currentButtonBorder);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, CORNER_RADIUS, CORNER_RADIUS);

                g2.setColor(getForeground());
                super.paintComponent(g2);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                d.height = FIELD_HEIGHT;
                return d;
            }
        };

        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setForeground(Color.WHITE);
        comboBox.setOpaque(false);
        comboBox.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(currentButtonBg);
                setForeground(Color.WHITE);
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return this;
            }
        });

        panel.remove(1);
        panel.add(comboBox, BorderLayout.CENTER);
        return panel;
    }

    private void startSeparatorAnimation() {
        if (separatorAnimationTimer != null && separatorAnimationTimer.isRunning()) {
            separatorAnimationTimer.stop();
        }

        final long startTime = System.currentTimeMillis();
        final int startX = separator.getParent().getX();
        final int endX = (int)(getWidth() * 0.25);

        separatorAnimationTimer = new Timer(16, e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            float progress = Math.min(1.0f, (float)elapsed / SEPARATOR_ANIMATION_DURATION);
            progress--;
            float easedProgress = progress * progress * progress + 1;

            int currentX = (int)(startX + (endX - startX) * easedProgress);
            separator.getParent().setLocation(currentX, 0);

            Component rightPanel = separator.getParent().getParent().getComponent(2);
            rightPanel.setBounds(currentX + 1, 0, getWidth() - currentX - 1, getHeight());

            separator.getParent().repaint();

            if (progress >= 1.0f) {
                separatorAnimationTimer.stop();
            }
        });
        separatorAnimationTimer.start();
    }

    private JPanel titleBar;

    private JPanel createTitleBar() {
        titleBar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                g2d.dispose();
            }
        };
        titleBar.setOpaque(false);
        titleBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel(getTitle()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
                super.paintComponent(g2d);
                g2d.dispose();
            }
        };
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        titleBar.add(titleLabel, BorderLayout.WEST);

        titleBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (animationTimer != null && animationTimer.isRunning()) {
                    animationTimer.stop();
                }
                mouseDownCompCoords = e.getPoint();
            }
        });

        titleBar.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point currCoords = e.getLocationOnScreen();
                setLocation(currCoords.x - mouseDownCompCoords.x, currCoords.y - mouseDownCompCoords.y);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Point finalPosition = getLocation();
                smoothMoveTo(finalPosition);
            }
        });

        JButton closeButton = createTitleButton("X", e -> System.exit(0), new Color(255, 80, 80));
        JButton minimizeButton = createTitleButton("-", e -> setState(Frame.ICONIFIED), Color.LIGHT_GRAY);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
                super.paintComponent(g2d);
                g2d.dispose();
            }
        };
        buttonPanel.setOpaque(false);
        buttonPanel.add(minimizeButton);
        buttonPanel.add(closeButton);
        titleBar.add(buttonPanel, BorderLayout.EAST);

        return titleBar;
    }

    private JButton createTitleButton(String text, ActionListener action, Color hoverColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
                super.paintComponent(g2d);
                g2d.dispose();
            }
        };
        button.setFont(new Font("Segoe UI", Font.PLAIN, text.equals("X") ? 18 : 34));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.addActionListener(action);
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setForeground(hoverColor);
            }
            public void mouseExited(MouseEvent e) {
                button.setForeground(Color.WHITE);
            }
        });
        return button;
    }

    private class RoundedButton extends JButton {
        private boolean hover = false;
        private boolean pressed = false;

        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    pressed = false;
                    repaint();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    pressed = true;
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    pressed = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (currentFadeComponent == this) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
            }

            Color bg = pressed ? currentButtonClick :
                    hover ? currentButtonHover : currentButtonBg;
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);

            g2.setColor(currentButtonBorder);
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, CORNER_RADIUS, CORNER_RADIUS);

            g2.setColor(getForeground());
            FontMetrics fm = g2.getFontMetrics();
            Rectangle r = fm.getStringBounds(getText(), g2).getBounds();
            int x = (getWidth() - r.width) / 2;
            int y = (getHeight() - r.height) / 2 + fm.getAscent();
            g2.drawString(getText(), x, y);

            g2.dispose();
        }
    }

    private void applyRoundedCorners() {
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS));
    }

    private void smoothMoveTo(Point target) {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        targetLocation = target;
        currentLocation = getLocation();
        long animationStartTime = System.currentTimeMillis();

        animationTimer = new Timer(16, e -> {
            long elapsed = System.currentTimeMillis() - animationStartTime;
            float progress = Math.min(1.0f, (float)elapsed / ANIMATION_DURATION);
            progress--;
            float easedProgress = progress * progress * progress + 1;

            int newX = (int)(currentLocation.x + (targetLocation.x - currentLocation.x) * easedProgress);
            int newY = (int)(currentLocation.y + (targetLocation.y - currentLocation.y) * easedProgress);
            setLocation(newX, newY);

            if (progress >= 1.0f) {
                animationTimer.stop();
                setLocation(targetLocation);
            }
        });
        animationTimer.start();
    }

    private void startThemeSwitcher() {
        if (themeSwitchTimer != null) themeSwitchTimer.stop();
        themeSwitchTimer = new Timer(THEME_SWITCH_INTERVAL, e -> switchTheme());
        themeSwitchTimer.start();
    }

    private void switchTheme() {
        currentThemeIndex = (currentThemeIndex + 1) % themes.length;
        activeTheme = themes[currentThemeIndex];
        backgroundPanel.setTheme(activeTheme);

        switch (activeTheme) {
            case 1:
                targetButtonBg = new Color(10, 10, 30);
                targetButtonBorder = new Color(10, 10, 40);
                break;
            case 2:
                targetButtonBg = new Color(10, 30, 10);
                targetButtonBorder = new Color(10, 40, 10);
                break;
            case 3:
                targetButtonBg = new Color(30, 10, 10);
                targetButtonBorder = new Color(40, 10, 10);
                break;
        }
        startButtonTransition();
    }

    private void startButtonTransition() {
        if (buttonTransitionTimer != null && buttonTransitionTimer.isRunning()) {
            buttonTransitionTimer.stop();
        }

        buttonTransitionProgress = 0f;
        buttonTransitionTimer = new Timer(16, e -> {
            buttonTransitionProgress += 16f / 1000;
            if (buttonTransitionProgress >= 1f) {
                buttonTransitionProgress = 1f;
                buttonTransitionTimer.stop();
            }

            currentButtonBg = interpolateColor(currentButtonBg, targetButtonBg, buttonTransitionProgress);
            currentButtonBorder = interpolateColor(currentButtonBorder, targetButtonBorder, buttonTransitionProgress);
            updateButtonColors();
        });
        buttonTransitionTimer.start();
    }

    private void updateButtonColors() {
        switch (activeTheme) {
            case 1:
                currentButtonHover = new Color(20, 20, 60);
                currentButtonClick = new Color(15, 15, 45);
                break;
            case 2:
                currentButtonHover = new Color(20, 60, 20);
                currentButtonClick = new Color(15, 45, 15);
                break;
            case 3:
                currentButtonHover = new Color(60, 20, 20);
                currentButtonClick = new Color(45, 15, 15);
                break;
        }
        nextButton.repaint();
        prevButton.repaint();
        navNextButton.repaint();
    }

    private Color interpolateColor(Color start, Color end, float progress) {
        int r = (int)(start.getRed() * (1 - progress) + end.getRed() * progress);
        int g = (int)(start.getGreen() * (1 - progress) + end.getGreen() * progress);
        int b = (int)(start.getBlue() * (1 - progress) + end.getBlue() * progress);
        return new Color(r, g, b);
    }

    @Override
    public void dispose() {
        if (animationTimer != null) animationTimer.stop();
        if (themeSwitchTimer != null) themeSwitchTimer.stop();
        if (separatorAnimationTimer != null) separatorAnimationTimer.stop();
        if (buttonTransitionTimer != null) buttonTransitionTimer.stop();
        if (fadeTimer != null) fadeTimer.stop();
        super.dispose();
    }
}