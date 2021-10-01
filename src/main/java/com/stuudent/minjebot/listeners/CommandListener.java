package com.stuudent.minjebot.listeners;

import com.stuudent.minjebot.API;
import com.stuudent.minjebot.Core;
import com.stuudent.minjebot.data.UserData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
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
                String prefix = Core.cf.getString("PREFIX");
                EmbedBuilder eb = new EmbedBuilder();
                eb.setColor(Color.WHITE);
                eb.setTitle("민제-봇 도움말");
                eb.addField("도움말 명령어", "```" + prefix + "```",false);
                eb.addField("업로드(Upload) 명령어", "```" + prefix + " 업로드 (00년 00월)```\n" +
                        "파일을 업로드하면서 해당 형식으로 댓글을 작성함으로써 사용함.\n" +
                        "날짜 생략시 현재 시간이 업로드 시점으로 작성됨.", false);
                eb.addField("열람(Show) 명령어", "```" + prefix + " 보여줘 (00년 00월)```\n" +
                        "업로드한 파일을 다시 전송해줌. 날짜 생략시 모두 다 전송함.", false);
                eb.addField("삭제(Delete) 명령어", "```" + prefix + " 지울게 file.abc```\n" +
                        "업로드한 파일을 삭제함. 파일명에는 반드시 확장자가 포함되어야함.", false);
                eb.setFooter("Created by 민제#5894");
                e.getChannel().sendMessage(e.getAuthor().getAsMention()).queue();
                e.getChannel().sendMessageEmbeds(eb.build()).queue();
            }
            else if(args[1].equals("업로드")) {
                List<Message.Attachment> attachments = e.getMessage().getAttachments();
                if(attachments.isEmpty()) return;
                Date lastDate = new Date(System.currentTimeMillis());
                if(args.length != 2) {
                    StringBuilder builder = new StringBuilder();
                    for(int i=2; i < args.length; i++) {
                        builder.append(args[i]).append(" ");
                    }
                    try {
                        lastDate = new SimpleDateFormat("yy년 MM월").parse(builder.toString());
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                        e.getChannel().sendMessage(e.getAuthor().getAsMention() + "\n```날짜를 잘못입력했습니다. \n" +
                                "날짜는 \"21년 10월\" 의 형식으로 입력해주세요.```").queue();
                        return;
                    }
                }
                for(Message.Attachment attachment : attachments) {
                    File path = new File("uploads/" + e.getAuthor().getId());
                    if (!path.exists())
                        path.mkdirs();
                    File attachedFile = new File(path + "/" + attachment.getFileName());
                    Date finalLastDate = lastDate;
                    attachment.downloadToFile(attachedFile).whenComplete((file, throwable) -> {
                        file.setLastModified(finalLastDate.getTime());
                    });
                }
                e.getMessage().addReaction("✅").queue();
                e.getChannel().sendMessage(e.getAuthor().getAsMention() + "\n```파일이 성공적으로 업로드 되었습니다.```").queue();
            }
            else if(args[1].equals("지울게")) {
                if(args.length == 2) {
                    e.getChannel().sendMessage(e.getAuthor().getAsMention() + "\n```업로드한 파일을 지우려면 \n"
                            + "\"" + Core.cf.getString("PREFIX") + " 지울게 picture1.png\" 의 형식으로 입력하세요.```").queue();
                    return;
                }
                StringBuilder builder = new StringBuilder();
                for(int i=2; i < args.length; i++) {
                    builder.append(args[i]);
                    if(i != args.length - 1)
                        builder.append(" ");
                }
                File file = new File("uploads/" + e.getAuthor().getId() + "/" + builder);
                if(!file.exists()) {
                    e.getChannel().sendMessage(e.getAuthor().getAsMention() + "\n```해당 파일이 존재하지 않습니다.```").queue();
                    return;
                }
                file.delete();
                e.getMessage().addReaction("✅").queue();
                e.getChannel().sendMessage(e.getAuthor().getAsMention() + "\n```파일이 성공적으로 삭제 되었습니다.```").queue();
            }
            else if(args[1].equals("보여줘")) {
                UserData userData = API.getUserData(e.getAuthor());
                if(args.length == 2) {
                    if(userData.getUploads(0, System.currentTimeMillis()).isEmpty()) {
                        e.getChannel().sendMessage(e.getAuthor().getAsMention() + "\n```현재 업로드한 파일이 없습니다.\n" +
                                "파일을 업로드하려면 파일을 업로드하면서 \"" + Core.cf.getString("PREFIX") + " 업로드\"" +
                                " 를 입력해주세요```").queue();
                    } else {
                        for(File file : userData.getUploads(0, System.currentTimeMillis())) {
                            e.getChannel().sendMessage("```파일 이름 : " + file.getName() + "\n"
                            + "업로드 시점 : " + new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초").format(new Date(file.lastModified()))
                            + "```").queue();
                            e.getChannel().sendFile(file).queue();
                        }
                        e.getChannel().sendMessage(e.getAuthor().getAsMention() + "\n```현재까지 업로드한 파일을 불러왔습니다.\n" +
                                "기간을 정하려면 \"" + Core.cf.getString("PREFIX") + " 보여줘 21년 10월\"" +
                                " 의 형식으로 입력해주세요```").queue();
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
                            e.getChannel().sendMessage(e.getAuthor().getAsMention() + "\n```해당 기간에 업로드한 파일이 없습니다.```").queue();
                        } else {
                            for (File file : userData.getUploads(startDate.getTime(), lastDate.getTime())) {
                                e.getChannel().sendMessage("```파일 이름 : " + file.getName() + "\n"
                                        + "업로드 시점 : " + new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초").format(new Date(file.lastModified()))
                                        + "```").queue();
                                e.getChannel().sendFile(file).queue();
                            }
                            e.getChannel().sendMessage(e.getAuthor().getAsMention() + "\n```해당 기간에 업로드한 파일을 불러왔습니다.\n" +
                                    "기간 : " + new SimpleDateFormat("yy년 MM월").format(startDate) + "```").queue();
                        }
                    } catch (NumberFormatException | ParseException ex) {
                        ex.printStackTrace();
                        e.getChannel().sendMessage(e.getAuthor().getAsMention() + "\n```날짜를 잘못입력했습니다. \n" +
                                "날짜는 \"21년 10월\" 의 형식으로 입력해주세요.```").queue();
                    }
                }
            }
        }
    }

}
