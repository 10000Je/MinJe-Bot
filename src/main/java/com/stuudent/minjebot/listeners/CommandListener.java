package com.stuudent.minjebot.listeners;

import com.stuudent.minjebot.API;
import com.stuudent.minjebot.Core;
import com.stuudent.minjebot.data.UserData;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CommandListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if(e.getAuthor().isBot()) return;
        String[] args = e.getMessage().getContentRaw().split(" ");
        if(args[0].equals(Core.cf.getString("PREFIX"))) {
            if(args.length == 1) {
                e.getChannel().sendMessage("ㅎㅇ").queue();
            }
            else if(args[1].equals("업로드")) {
                List<Message.Attachment> attachments = e.getMessage().getAttachments();
                if(attachments.isEmpty()) return;
                for(Message.Attachment attachment : attachments) {
                    File path = new File("uploads/" + e.getAuthor().getId());
                    if (!path.exists())
                        path.mkdirs();
                    attachment.downloadToFile(path + "/" + attachment.getFileName());
                }
                e.getMessage().addReaction("✅").queue();
                e.getChannel().sendMessage("```파일이 성공적으로 업로드 되었습니다.```").queue();
            }
            else if(args[1].equals("보여줘")) {
                UserData userData = API.getUserData(e.getAuthor());
                if(args.length == 2) {
                    if(userData.getUploads(0, System.currentTimeMillis()).isEmpty()) {
                        e.getChannel().sendMessage("```현재 업로드한 파일이 없습니다.\n" +
                                "파일을 업로드하려면 파일을 업로드하면서 \"" + Core.cf.getString("PREFIX") + " 업로드\"" +
                                " 를 입력해주세요```").queue();
                    } else {
                        for(File file : userData.getUploads(0, System.currentTimeMillis()))
                            e.getChannel().sendFile(file).queue();
                        e.getChannel().sendMessage("```현재까지 업로드한 파일을 불러왔습니다.\n" +
                                "기간을 정하려면 \"" + Core.cf.getString("PREFIX") + " 보여줘 [00년 00월]\"" +
                                " 처럼 입력해주세요```").queue();
                    }
                } else {
                    StringBuilder builder = new StringBuilder();
                    for(int i=2; i < args.length; i++) {
                        builder.append(args[i]).append(" ");
                    }
                    try {
                        Date startDate = new SimpleDateFormat("yy년 MM월").parse(builder.toString());
                        int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(startDate));
                        int month = Integer.parseInt(new SimpleDateFormat("MM").format(startDate));
                        int day = 1;
                        Calendar cal = Calendar.getInstance();
                        cal.set(year, month, day);
                        Date lastDate = new SimpleDateFormat("yy년 MM월 dd일 HH시 mm분 ss초").parse(builder.toString()
                                + cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                                + "일" + " 23시" + " 59분" + " 59초");
                        if(userData.getUploads(startDate.getTime(), lastDate.getTime()).isEmpty()) {
                            e.getChannel().sendMessage("```해당 기간에 업로드한 파일이 없습니다.```").queue();
                        } else {
                            for (File file : userData.getUploads(startDate.getTime(), lastDate.getTime()))
                                e.getChannel().sendFile(file).queue();
                            e.getChannel().sendMessage("```해당 기간에 업로드한 파일을 불러왔습니다.\n" +
                                    "기간 : " + new SimpleDateFormat("yy년 MM월").format(startDate) + "```").queue();
                        }
                    } catch (NumberFormatException | ParseException ex) {
                        ex.printStackTrace();
                        e.getChannel().sendMessage("```날짜를 잘못입력했습니다. \n" +
                                "날짜는 00년 00월 의 형식으로 입력해주세요.```").queue();
                    }
                }
            }
        }
    }

}
