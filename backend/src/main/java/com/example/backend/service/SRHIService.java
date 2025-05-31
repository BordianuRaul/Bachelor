package com.example.backend.service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SRHIService {

    private static final int VERY_WEAK_HABIT = 29;
    private static final int WEAK_HABIT = 47;
    private static final int MODERATE_HABIT = 65;
    private static final int STRONG_HABIT = 84;
    private final Random random;
    private final HashMap<String, List<String>> messages;

    public SRHIService() {
        random = new Random();
        messages = new HashMap<>();

        messages.put("veryWeak", Arrays.asList(
                "This behavior shows little to no signs of habitual engagement. It is likely performed consciously and infrequently, without automaticity.",
                "You may engage in this behavior occasionally, but it does not appear to be part of a regular pattern. It likely requires deliberate intention each time.",
                "At this stage, the behavior doesn't exhibit signs of becoming a habit. It remains a conscious decision rather than an automatic response."
        ));

        messages.put("weak", Arrays.asList(
                "This behavior is performed more regularly but still lacks strong automatic characteristics. Some routines may be forming, but they’re not consistent yet.",
                "There are emerging signs of repetition, but the behavior is still largely conscious. Continued engagement could lead to stronger habit formation.",
                "The behavior is beginning to stabilize, but may still depend on external cues or motivation rather than internal automaticity."
        ));

        messages.put("moderate", Arrays.asList(
                "This behavior is somewhat established and may be partially automatic. You may perform it in certain situations without much deliberation.",
                "There is a balance between conscious control and automatic repetition. The behavior is forming a reliable pattern in your routine.",
                "This behavior occurs with moderate consistency. While not entirely automatic, it may require less effort or thought compared to earlier stages."
        ));

        messages.put("strong", Arrays.asList(
                "This behavior is consistently repeated and shows many characteristics of automaticity. It likely fits into your daily or weekly routine naturally.",
                "The behavior appears to be well-integrated into your life and occurs with minimal conscious effort. It's becoming second nature in many contexts.",
                "You are likely to perform this behavior in familiar environments without active decision-making. It has formed strong associations in your routine."
        ));

        messages.put("veryStrong", Arrays.asList(
                "This behavior is deeply ingrained and occurs with high automaticity. It may be difficult to avoid or even notice when it begins.",
                "The behavior is strongly internalized and performed with little or no conscious awareness. It may be closely tied to your identity or self-image.",
                "You likely initiate this behavior before you even realize it. It functions as a default response in relevant situations, indicating full habit strength."
        ));
    }

    private int computeSRHIScore(final List<Integer> scores) {
        return scores.stream().reduce(0, Integer::sum);
    }

    private String buildSRHIMessage(int score) {
        List<String> habitMessages;

        if (score <= VERY_WEAK_HABIT) {
            habitMessages = messages.get("veryWeak");
        } else if (score <= WEAK_HABIT) {
            habitMessages = messages.get("weak");
        } else if (score <= MODERATE_HABIT) {
            habitMessages = messages.get("moderate");
        } else if (score <= STRONG_HABIT) {
            habitMessages = messages.get("strong");
        } else {
            habitMessages = messages.get("veryStrong");
        }

        return habitMessages.get(random.nextInt(habitMessages.size()));
    }

    public String getInformationAboutTheHabitsSRHI(final List<Integer> answers) {
        final int scores = computeSRHIScore(answers);
        return buildSRHIMessage(scores);
    }
}