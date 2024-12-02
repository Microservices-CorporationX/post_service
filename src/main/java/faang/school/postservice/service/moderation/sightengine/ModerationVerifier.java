package faang.school.postservice.service.moderation.sightengine;

import faang.school.postservice.service.moderation.sightengine.check.ModerationCheck;
import faang.school.postservice.service.moderation.sightengine.check.impl.DiscriminatoryCheck;
import faang.school.postservice.service.moderation.sightengine.check.impl.InsultingCheck;
import faang.school.postservice.service.moderation.sightengine.check.impl.SexualCheck;
import faang.school.postservice.service.moderation.sightengine.check.impl.ViolentCheck;

import java.util.ArrayList;
import java.util.List;

public class ModerationVerifier {

    private final List<ModerationCheck> checks = new ArrayList<>();

    public boolean verify() {
        for (ModerationCheck check : checks) {
            if (!check.check()) return false;
        }
        return true;
    }

    public ModerationVerifier sexual(double value) {
        checks.add(new SexualCheck(value));
        return this;
    }

    public ModerationVerifier discriminatory(double value) {
        checks.add(new DiscriminatoryCheck(value));
        return this;
    }

    public ModerationVerifier insulting(double value) {
        checks.add(new InsultingCheck(value));
        return this;
    }

    public ModerationVerifier violent(double value) {
        checks.add(new ViolentCheck(value));
        return this;
    }

    public ModerationVerifier toxic(double value) {
        checks.add(new ViolentCheck(value));
        return this;
    }
}
