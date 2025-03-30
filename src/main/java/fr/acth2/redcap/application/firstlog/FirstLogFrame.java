package fr.acth2.redcap.application.firstlog;

import fr.acth2.redcap.effects.MainBackgroundPanel;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import static fr.acth2.redcap.log.Logger.*;

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
        createUIComponents();
        setupUI();
        applyRoundedCorners();

        Timer initialDelayTimer = new Timer(500, e -> {
            startThemeSwitcher();
        });
        initialDelayTimer.setRepeats(false);
        initialDelayTimer.start();
    }

    private void startThemeSwitcher() {
        if (themeSwitchTimer != null) {
            themeSwitchTimer.stop();
        }

        themeSwitchTimer = new Timer(THEME_SWITCH_INTERVAL, e -> switchTheme());
        themeSwitchTimer.start();
    }

    private void applyRoundedCorners() {
        setShape(new RoundRectangle2D.Double(
                0, 0,
                getWidth(), getHeight(),
                CORNER_RADIUS, CORNER_RADIUS
        ));
    }

    private void createUIComponents() {
        log("Loading FirstLogFrame UI Components");

        currentButtonBg = new Color(10, 30, 10);
        targetButtonBg = currentButtonBg;
        currentButtonBorder = new Color(10, 40, 10);
        targetButtonBorder = currentButtonBorder;
        currentButtonHover = new Color(20, 60, 20);
        currentButtonClick = new Color(15, 45, 15);

        switch (activeTheme) {
            case 1:
                currentButtonBg = new Color(10, 10, 30);
                targetButtonBg = currentButtonBg;
                currentButtonBorder = new Color(10, 10, 40);
                targetButtonBorder = currentButtonBorder;
                currentButtonHover = new Color(20, 20, 60);
                currentButtonClick = new Color(15, 15, 45);
                break;
            case 3:
                currentButtonBg = new Color(30, 10, 10);
                targetButtonBg = currentButtonBg;
                currentButtonBorder = new Color(40, 10, 10);
                targetButtonBorder = currentButtonBorder;
                currentButtonHover = new Color(60, 20, 20);
                currentButtonClick = new Color(45, 15, 15);
                break;
        }

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

        welcomeLabel = new JLabel("Welcome to RedCap", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(60, 0, 0, 0));

        nextButton = new RoundedButton("NEXT");
        nextButton.setFont(new Font("Segoe UI", Font.BOLD, 22));
        nextButton.setPreferredSize(new Dimension(300, 80));
        nextButton.setForeground(Color.WHITE);
        nextButton.setFocusPainted(false);
        updateButtonColors();
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

    private Border createRoundedBorder(Color color, int thickness, int radius) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, thickness),
                BorderFactory.createEmptyBorder(15, 30, 15, 30)
        );
    }

    private void setupUI() {
        log("Setting up FirstLogFrame Taskbar");
        JPanel titleBar = new JPanel(new BorderLayout()) {
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

        JLabel titleLabel = new JLabel(getTitle());
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
                setLocation(
                        currCoords.x - mouseDownCompCoords.x,
                        currCoords.y - mouseDownCompCoords.y
                );
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Point finalPosition = getLocation();
                smoothMoveTo(finalPosition);
            }
        });

        JButton closeButton = new JButton("X");
        closeButton.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        closeButton.setForeground(Color.WHITE);
        closeButton.setContentAreaFilled(false);
        closeButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        closeButton.addActionListener(e -> System.exit(0));
        closeButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                closeButton.setForeground(new Color(255, 80, 80));
            }
            public void mouseExited(MouseEvent e) {
                closeButton.setForeground(Color.WHITE);
            }
        });

        JButton minimizeButton = new JButton("-");
        minimizeButton.setFont(new Font("Segoe UI", Font.PLAIN, 34));
        minimizeButton.setForeground(Color.WHITE);
        minimizeButton.setContentAreaFilled(false);
        minimizeButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        minimizeButton.addActionListener(e -> setState(Frame.ICONIFIED));
        minimizeButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                minimizeButton.setForeground(Color.LIGHT_GRAY);
            }
            public void mouseExited(MouseEvent e) {
                minimizeButton.setForeground(Color.WHITE);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(minimizeButton);
        buttonPanel.add(closeButton);
        titleBar.add(buttonPanel, BorderLayout.EAST);

        log("Setting up FirstLogFrame UI");
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 50, 0);

        centerPanel.add(welcomeLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(20, 0, 0, 0);
        centerPanel.add(nextButton, gbc);

        backgroundPanel.add(titleBar, BorderLayout.NORTH);
        backgroundPanel.add(centerPanel, BorderLayout.CENTER);

        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(
                BORDER_THICKNESS, BORDER_THICKNESS,
                BORDER_THICKNESS, BORDER_THICKNESS
        ));

        setContentPane(backgroundPanel);

        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
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

        if (currentButtonBg == null) {
            currentButtonBg = targetButtonBg;
            currentButtonBorder = targetButtonBorder;
            updateButtonColors();
            return;
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
    }

    private Color interpolateColor(Color start, Color end, float progress) {
        int r = (int) (start.getRed() * (1 - progress) + end.getRed() * progress);
        int g = (int) (start.getGreen() * (1 - progress) + end.getGreen() * progress);
        int b = (int) (start.getBlue() * (1 - progress) + end.getBlue() * progress);
        return new Color(r, g, b);
    }

    private void updateButtonTheme() {
        switch (activeTheme) {
            case 1:
                nextButton.setBackground(new Color(10, 10, 30));
                break;
            case 2:
                nextButton.setBackground(new Color(10, 30, 10));
                break;
            case 3:
                nextButton.setBackground(new Color(30, 10, 10));
                break;
        }
        nextButton.repaint();
    }

    @Override
    public void dispose() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
        if (themeSwitchTimer != null) {
            themeSwitchTimer.stop();
        }
        super.dispose();
    }
}