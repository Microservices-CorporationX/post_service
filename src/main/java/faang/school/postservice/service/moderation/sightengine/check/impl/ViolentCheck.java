package faang.school.postservice.service.moderation.sightengine.check.impl;

import faang.school.postservice.config.verification.content.ModerationTargets;
import faang.school.postservice.service.moderation.sightengine.check.ModerationCheck;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ViolentCheck implements ModerationCheck {

    private final double value;

    @Override
    public boolean check() {
        return value <= ModerationTargets.VIOLENT;
    }
}
