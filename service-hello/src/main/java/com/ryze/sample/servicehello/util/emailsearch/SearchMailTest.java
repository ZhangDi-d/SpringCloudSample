package com.ryze.sample.servicehello.util.emailsearch;


import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.search.AndTerm;
import javax.mail.search.BodyTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.FromStringTerm;
import javax.mail.search.IntegerComparisonTerm;
import javax.mail.search.NotTerm;
import javax.mail.search.OrTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SentDateTerm;
import javax.mail.search.SizeTerm;
import javax.mail.search.SubjectTerm;
/**
 * Created by xueLai on 2020/3/20.
 */
public class SearchMailTest {

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.setProperty("mail.pop3.auth", "true");
        Session session = Session.getInstance(props);
        URLName url = new URLName("pop3", "pop3.163.com", 110, null, "xyang81@163.com", "yX546900873");
        Store store = session.getStore(url);
        store.connect();
        // 得到收件箱
        Folder folder = store.getFolder("INBOX");
        // 以读写模式打开收件箱
        folder.open(Folder.READ_WRITE);

        Message[] messages = search(folder);

        System.out.println("收件箱中共有:" + folder.getMessageCount() + "封邮件，搜索到" + messages.length + "封符合条件的邮件!");

        // 根据用户输入的条件搜索所有邮件,并提示用户是否删除
        //searchDemo(folder);

        folder.close(true);
        store.close();
    }

    public static Message[] search(Folder folder) throws Exception {
        // 搜索主题包含美食的邮件
        String subject = "java培训";
        SearchTerm subjectTerm = new SubjectTerm(subject);

        // 搜索发件人包含支付宝的邮件
        SearchTerm fromTerm = new FromStringTerm("支付宝");

        // 搜索邮件内容包含"招聘"的邮件
        SearchTerm bodyTerm = new BodyTerm("招聘");

        // 搜索发件人不包含“智联招聘”的邮件
        SearchTerm notTerm = new NotTerm(new FromStringTerm("智联招聘"));

        // 搜索发件人为“智联招聘”，而且内容包含“Java工程师“的邮件
        SearchTerm andTerm = new AndTerm(
                new FromStringTerm("智联招聘"),
                new BodyTerm("java工程师"));


        // 搜索发件人为”智联招聘“或主题包含”最新职位信息“的邮件
        SearchTerm orTerm = new OrTerm(
                new FromStringTerm("智联招聘"),
                new SubjectTerm("最新职位信息"));


        // 搜索周一到今天收到的的所有邮件
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.get(Calendar.DAY_OF_WEEK - (Calendar.DAY_OF_WEEK - 1)) - 1);
        Date mondayDate = calendar.getTime();
        SearchTerm comparisonTermGe = new SentDateTerm(ComparisonTerm.GE, mondayDate);
        SearchTerm comparisonTermLe = new SentDateTerm(ComparisonTerm.LE, new Date());
        SearchTerm comparisonAndTerm = new AndTerm(comparisonTermGe, comparisonTermLe);

        // 搜索大于或等100KB的所有邮件
        int mailSize = 1024 * 100;
        SearchTerm intComparisonTerm = new SizeTerm(IntegerComparisonTerm.GE, mailSize);

        return folder.search(intComparisonTerm);
    }
}