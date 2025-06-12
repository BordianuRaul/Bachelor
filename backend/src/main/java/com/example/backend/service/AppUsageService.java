package com.example.backend.service;

import com.example.backend.dto.ApplicationDailyUsageDTO;
import com.example.backend.model.Application;
import com.example.backend.model.ApplicationHourlyUsage;
import com.example.backend.model.User;
import com.example.backend.repository.ApplicationHourlyUsageRepository;
import com.example.backend.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AppUsageService {

    private static final int APPLICATION_USAGES_THRESHOLD = 4;
    private final ApplicationHourlyUsageRepository applicationHourlyUsageRepository;
    private final ApplicationRepository applicationRepository;
    private final Map<String, Integer> applicationUsageIntervals = new HashMap<>();
    private final Random random = new Random();
    private final List<String> weakMessages = List.of(
            "You’ve used **%s** frequently over the past week. This might suggest a budding habit.",
            "Repeated usage of **%s** indicates a possible shift toward routine behavior.",
            "Your engagement with **%s** is becoming more regular — perhaps something to keep an eye on.",
            "You're opening **%s** quite a bit. While not yet a strong pattern, it's a notable trend.",
            "**%s** seems to be catching your attention more often lately. Could this be a habit in the making?"
    );
    private final List<String> mildMessages = List.of(
            "You’ve used **%s** regularly during **%s**, hinting at a habit triggered by time of day.",
            "Your pattern with **%s** around **%s** suggests a mild habit forming around that time slot.",
            "**%s** shows consistent use in the **%s** window — this may be a cue-driven habit.",
            "You tend to open **%s** during **%s**, indicating a possible behavioral routine.",
            "There's a noticeable rhythm to your **%s** usage in **%s**. Habits often start this way."
    );

    @Autowired
    public AppUsageService(ApplicationHourlyUsageRepository applicationHourlyUsageRepository, ApplicationRepository applicationRepository) {
        this.applicationHourlyUsageRepository = applicationHourlyUsageRepository;
        this.applicationRepository = applicationRepository;
    }
    
    public LocalDate getLastUpdateDateForUser(UUID userId) {
        LocalDate lastDate = applicationHourlyUsageRepository.findLatestDateByUserId(userId);
        return lastDate == null ? LocalDate.now().minusDays(7) : lastDate;
    }

    public void updateAppUsage(User user, List<ApplicationDailyUsageDTO> appsUsages) {
        for(ApplicationDailyUsageDTO currentApplication : appsUsages) {
            Application application = applicationRepository.findByName(currentApplication.getName())
                    .orElse(null);
            final LocalDate date = currentApplication.getDate();
            final List<Integer> hourlyUsages = currentApplication.getHourlyUsageInSeconds();
            for(int hour = 0; hour <= 23; hour++) {
                final int secondsSpent = hourlyUsages.get(hour);
                if(secondsSpent == 0) {
                    continue;
                }
                final ApplicationHourlyUsage applicationHourlyUsage = new ApplicationHourlyUsage(user, application, hour, secondsSpent, date);
                applicationHourlyUsageRepository.save(applicationHourlyUsage);
            }
        }
    }

    public List<String> findPossibleHabits(User user) {
        final List<Application> applications = applicationRepository.findAll();
        List<String> habitMessages = new ArrayList<>();

        for (Application application : applications) {
            boolean weakIndication = false;
            boolean mildIndication = false;
            UUID applicationId = UUID.fromString(application.getId());

            if (hasWeakUsagePattern(applicationId, user.getId())) {
                weakIndication = true;
            }

            if (weakIndication && hasMildUsagePattern(applicationId.toString(), user.getId())) {
                mildIndication = true;
            }

            if (weakIndication) {
                String habitMessage = buildPossibleHabitMessage(application, weakIndication, mildIndication);
                habitMessages.add(habitMessage);
            }
        }

        return habitMessages;
    }



    private String buildPossibleHabitMessage(Application app, boolean weakIndication, boolean mildIndication) {
        String appName = app.getName();

        if (mildIndication && applicationUsageIntervals.containsKey(app.getId())) {
            int interval = applicationUsageIntervals.get(app.getId());
            String intervalStr = convertIntervalToReadableRange(interval);
            String template = mildMessages.get(random.nextInt(mildMessages.size()));
            return String.format(template, appName, intervalStr);
        } else if (weakIndication) {
            String template = weakMessages.get(random.nextInt(weakMessages.size()));
            return String.format(template, appName);
        }

        return "";
    }

    private String convertIntervalToReadableRange(int intervalIndex) {
        int startHour = intervalIndex * 2;
        int endHour = startHour + 2;
        return String.format("%02d:00–%02d:00", startHour, endHour);
    }

    private boolean hasWeakUsagePattern(UUID applicationId, UUID userId) {
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(6);
        int usageDays = applicationHourlyUsageRepository.countUsageDaysInLastSevenDays(applicationId.toString(), userId, sevenDaysAgo);
        return usageDays >= APPLICATION_USAGES_THRESHOLD;
    }

    private boolean hasMildUsagePattern(String applicationId, UUID userId) {
        LocalDate fromDate = LocalDate.now().minusDays(6);
        List<ApplicationHourlyUsage> usageData = applicationHourlyUsageRepository
                .findByApplicationIdAndUserIdAndDateAfter(applicationId, userId, fromDate);

        Map<LocalDate, List<ApplicationHourlyUsage>> usagePerDay = usageData.stream()
                .collect(Collectors.groupingBy(ApplicationHourlyUsage::getDate));

        Map<Integer, Integer> intervalCount = new HashMap<>();
        int totalDaysWithUsage = usagePerDay.size();

        for (Map.Entry<LocalDate, List<ApplicationHourlyUsage>> entry : usagePerDay.entrySet()) {
            int[] intervalUsage = new int[12];
            for (ApplicationHourlyUsage usage : entry.getValue()) {
                int intervalIndex = usage.getHour() / 2;
                intervalUsage[intervalIndex] += usage.getSecondsSpent();
            }

            int maxInterval = 0;
            for (int i = 1; i < 12; i++) {
                if (intervalUsage[i] > intervalUsage[maxInterval]) {
                    maxInterval = i;
                }
            }

            intervalCount.put(maxInterval, intervalCount.getOrDefault(maxInterval, 0) + 1);
        }

        for (Map.Entry<Integer, Integer> entry : intervalCount.entrySet()) {
            if (entry.getValue() >= (totalDaysWithUsage / 2)) {
                applicationUsageIntervals.put(applicationId, entry.getKey());
                return true;
            }
        }

        return false;
    }
}
