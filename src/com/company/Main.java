package com.company;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;

public class Main {
    public static void main(String[] args) {
        double sum;
        double GPA;
        String username = "2015300955";
        String password = "J2mv9jyyq6";
        try {
            String param = "username=" + username + "&password=" + password;
            URL url = new URL("http://us.nwpu.edu.cn/eams/login.action");

            //获取Cookie
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            System.out.println(connection.getHeaderFields());
            String cookie = connection.getHeaderField("Set-Cookie");


            //模拟登陆
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Cookie", cookie);

            OutputStream os = connection.getOutputStream();
            os.write(param.getBytes("GBK"));
            os.close();

            //并不知道为什么加这个，但是不加这个下面就无法正常获取数据
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            br.close();


            //获取成绩
            url = new URL("http://us.nwpu.edu.cn/eams/teach/grade/course/person!historyCourseGrade.action?projectType=MAJOR");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Cookie", cookie);

            //获取网页源代码
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
            StringBuilder builder = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null){
                builder.append(line + '\n');
            }
            reader.close();
            sum = 0;
            GPA = 0;

            //利用JSoup解析
            Document document = Jsoup.parse(builder.toString());
            Elements trTags = document.select("tr");
            for (Element trTag : trTags){
                Elements tdTags = trTag.select("td");
                if (tdTags.size()==0){
                    continue;
                }
                if (!tdTags.get(0).text().equals("2016-2017 秋")){
                    continue;
                }
                int i = 1;
                boolean gpaEnable = true;
                double credit = 0;
                builder = new StringBuilder();
                for (Element tdTag : tdTags){
                    switch (i){
                        case 2:
//                                System.out.println(tdTag.text());
                            if (tdTag.text().charAt(3) == 'L'){
                                gpaEnable = false;
                            }else{
                                gpaEnable = true;
                            }
                            break;
                        case 4:
//                            map.put("name",tdTag.text());
                            System.out.println(tdTag.text());
                            break;
                        case 6:
                            if (!gpaEnable){
                                i++;
                                continue;
                            }
                            credit = Double.valueOf(tdTag.text());
                            sum += credit;
                            break;
                        case 7:
                            builder.append("平时成绩：" + tdTag.text() + '\t');
                            break;
                        case 8:
                            builder.append("期中成绩：" + tdTag.text() + '\n');
                            break;
                        case 10:
                            builder.append("期末成绩：" + tdTag.text() + '\t');
                            break;
                        case 11:
                            builder.append("总评成绩：" + tdTag.text() + '\n');
                            break;
                        case 12:
                            if (!gpaEnable){
                                i++;
                                continue;
                            }
//                            System.out.println("加入GPA");
                            GPA = GPA + credit * Double.valueOf(tdTag.text());
                            break;
                    }
                    i++;
                }
                System.out.println(builder.toString());
            }
            System.out.println("学分绩：" + (double)GPA/sum);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
