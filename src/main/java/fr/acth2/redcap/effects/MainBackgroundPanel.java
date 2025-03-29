package fr.acth2.redcap.effects;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainBackgroundPanel extends JPanel {
    private static final int BALL_COUNT = 24;
    private static final int RADIUS = 85;
    private static final float MAX_SPEED = 5.7f;
    private static final float DAMPING = 0.92f;
    private static final int BLUR_LAYERS = 2;
    private static final float MAX_ALPHA = 0.005f;
    private static final float ALPHA_STEP = 0.0005f;
    private static final float SIZE_STEP = 0.25f;
    private Color currentBgColor;
    private Color targetBgColor;
    private Color currentBallColor;
    private Color targetBallColor;
    private Timer transitionTimer;
    private static final int TRANSITION_DURATION = 1000;

    private final List<FloatingBall> balls = new ArrayList<>();
    private final Random random = new Random();
    private BufferedImage backBuffer;
    private Timer animationTimer;

    public MainBackgroundPanel(int colorCode) {
        currentBgColor = getColorForTheme(colorCode);
        targetBgColor = currentBgColor;
        currentBallColor = getBallColorForTheme(colorCode);
        targetBallColor = currentBallColor;
        setBackground(currentBgColor);

        setOpaque(false);
        setLayout(new BorderLayout());
        initializeBalls();
        startAnimation();
    }

    private Color getColorForTheme(int colorCode) {
        switch (colorCode) {
            case 1: return new Color(10, 10, 20);
            case 2: return new Color(10, 20, 10);
            case 3: return new Color(20, 10, 10);
            default: return new Color(10, 20, 10);
        }
    }

    private Color getBallColorForTheme(int colorCode) {
        switch (colorCode) {
            case 1: return new Color(50, 50, 255);
            case 2: return new Color(50, 255, 50);
            case 3: return new Color(255, 50, 50);
            default: return new Color(50, 255, 50);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Paint background
        g.setColor(currentBgColor);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Create or update back buffer if needed
        if (backBuffer == null ||
                backBuffer.getWidth() != getWidth() ||
                backBuffer.getHeight() != getHeight()) {
            createBackBuffer();
        }

        // Draw back buffer
        if (backBuffer != null) {
            g.drawImage(backBuffer, 0, 0, this);
        }
    }

    private void createBackBuffer() {
        if (getWidth() <= 0 || getHeight() <= 0) return;

        backBuffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = backBuffer.createGraphics();

        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setComposite(AlphaComposite.SrcOver);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (FloatingBall ball : balls) {
            for (int i = 0; i < BLUR_LAYERS; i++) {
                float alpha = Math.max(0, MAX_ALPHA - (i * ALPHA_STEP));
                float radius = RADIUS * (1 + i * SIZE_STEP);

                Color color = new Color(
                        currentBallColor.getRed(),
                        currentBallColor.getGreen(),
                        currentBallColor.getBlue(),
                        (int)(alpha * 255)
                );
                g2d.setColor(color);
                g2d.fill(new Ellipse2D.Float(
                        ball.x - radius,
                        ball.y - radius,
                        radius * 2,
                        radius * 2
                ));
            }
        }

        g2d.dispose();
    }

    public void setTheme(int colorCode) {
        Color newBgTarget = getColorForTheme(colorCode);
        Color newBallTarget = getBallColorForTheme(colorCode);

        if (!newBgTarget.equals(targetBgColor)) {
            targetBgColor = newBgTarget;
            targetBallColor = newBallTarget;
            startColorTransition();
        }
    }

    private void startColorTransition() {
        if (transitionTimer != null && transitionTimer.isRunning()) {
            transitionTimer.stop();
        }

        final float[] transitionProgress = {0f};
        transitionTimer = new Timer(16, e -> {
            transitionProgress[0] += 16f / TRANSITION_DURATION;

            if (transitionProgress[0] >= 1f) {
                transitionProgress[0] = 1f;
                currentBgColor = targetBgColor;
                currentBallColor = targetBallColor;
                transitionTimer.stop();
            } else {
                currentBgColor = interpolateColor(currentBgColor, targetBgColor, transitionProgress[0]);
                currentBallColor = interpolateColor(currentBallColor, targetBallColor, transitionProgress[0]);
            }

            setBackground(currentBgColor);
            createBackBuffer();
            repaint();
        });
        transitionTimer.start();
    }

    private Color interpolateColor(Color start, Color end, float progress) {
        if (start == null) return end;
        if (end == null) return start;

        int r = (int) (start.getRed() * (1 - progress) + end.getRed() * progress);
        int g = (int) (start.getGreen() * (1 - progress) + end.getGreen() * progress);
        int b = (int) (start.getBlue() * (1 - progress) + end.getBlue() * progress);
        return new Color(r, g, b);
    }

    private void initializeBalls() {
        balls.clear();
        int width = Math.max(getWidth(), 800);
        int height = Math.max(getHeight(), 600);

        for (int i = 0; i < BALL_COUNT; i++) {
            balls.add(new FloatingBall(
                    width/2 + random.nextInt(200) - 100,
                    height/2 + random.nextInt(200) - 100,
                    (random.nextFloat() - 0.5f) * MAX_SPEED,
                    (random.nextFloat() - 0.5f) * MAX_SPEED
            ));
        }
    }

    private void startAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }

        animationTimer = new Timer(30, e -> {
            boolean changed = false;

            for (FloatingBall ball : balls) {
                float prevX = ball.x;
                float prevY = ball.y;

                ball.update(getWidth(), getHeight());

                if (ball.x != prevX || ball.y != prevY) {
                    changed = true;
                }
            }

            if (changed && isShowing()) {
                createBackBuffer();
                repaint();
            }
        });
        animationTimer.start();
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (animationTimer != null) {
            animationTimer.stop();
        }
        if (transitionTimer != null) {
            transitionTimer.stop();
        }
    }

    private static class FloatingBall {
        float x, y, dx, dy;

        FloatingBall(float x, float y, float dx, float dy) {
            this.x = x;
            this.y = y;
            this.dx = dx;
            this.dy = dy;
        }

        void update(int width, int height) {
            x += dx;
            y += dy;

            if (x < RADIUS) {
                x = RADIUS;
                dx = -dx * DAMPING;
            } else if (x > width - RADIUS) {
                x = width - RADIUS;
                dx = -dx * DAMPING;
            }

            if (y < RADIUS) {
                y = RADIUS;
                dy = -dy * DAMPING;
            } else if (y > height - RADIUS) {
                y = height - RADIUS;
                dy = -dy * DAMPING;
            }
        }
    }
}