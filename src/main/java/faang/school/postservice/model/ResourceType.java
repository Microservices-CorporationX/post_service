package faang.school.postservice.model;

public enum ResourceType {
    VIDEO,
    AUDIO,
    IMAGE,
    TEXT,
    PDF,
    MSWORD,
    MSEXCEL,
    ZIP,
    OTHER;

    public static ResourceType getResourceType(String contentType) {
        if (contentType.contains("image")) {
            return ResourceType.IMAGE;
        } else if (contentType.contains("video")) {
            return ResourceType.VIDEO;
        } else if (contentType.contains("audio")) {
            return ResourceType.AUDIO;
        } else if (contentType.contains("msword")) {
            return ResourceType.MSWORD;
        } else if (contentType.contains("ms-excel")) {
            return ResourceType.MSEXCEL;
        } else if (contentType.contains("pdf")) {
            return ResourceType.PDF;
        } else if (contentType.contains("zip")) {
            return ResourceType.ZIP;
        } else if (contentType.contains("text")) {
            return ResourceType.TEXT;
        } else {
            return ResourceType.OTHER;
        }
    }
}
