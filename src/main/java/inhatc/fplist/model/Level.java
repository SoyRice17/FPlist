package inhatc.fplist.model;

public class Level {
    private String levelName;
    private String levelLink;
    private FpsData fpsData;
    private int rating;
    private Status status;
    private String levelId;
    private String comment;

    // 기본 생성자
    public Level() {}

    // Getters and Setters
    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getLevelLink() {
        return levelLink;
    }

    public void setLevelLink(String levelLink) {
        this.levelLink = levelLink;
    }

    public FpsData getFpsData() {
        return fpsData;
    }

    public void setFpsData(FpsData fpsData) {
        this.fpsData = fpsData;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getLevelId() {
        return levelId;
    }

    public void setLevelId(String levelId) {
        this.levelId = levelId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    // 내부 클래스들
    public static class FpsData {
        private int fps60;
        private int fps120;
        private int fps240;

        public FpsData() {}

        public int getFps60() {
            return fps60;
        }

        public void setFps60(int fps60) {
            this.fps60 = fps60;
        }

        public int getFps120() {
            return fps120;
        }

        public void setFps120(int fps120) {
            this.fps120 = fps120;
        }

        public int getFps240() {
            return fps240;
        }

        public void setFps240(int fps240) {
            this.fps240 = fps240;
        }
    }

    public static class Status {
        private String code;
        private String description;

        public Status() {}

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
} 