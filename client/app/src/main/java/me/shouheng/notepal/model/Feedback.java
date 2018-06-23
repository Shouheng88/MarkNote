package me.shouheng.notepal.model;

import me.shouheng.notepal.model.enums.FeedbackType;

/**
 * Created by wangshouheng on 2017/12/3.*/
public class Feedback extends Model {

    private String email;

    private String question;

    private FeedbackType feedbackType;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public FeedbackType getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(FeedbackType feedbackType) {
        this.feedbackType = feedbackType;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "email='" + email + '\'' +
                ", question='" + question + '\'' +
                ", feedbackType=" + (feedbackType == null ? null : feedbackType.name()) +
                "} " + super.toString();
    }
}
