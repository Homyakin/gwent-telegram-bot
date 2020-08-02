FROM bellsoft/liberica-openjdk-centos:14.0.2-13
COPY ./target/gwent-telegram-bot-1.0.jar /home/gwent-telegram-bot-1.0.jar
CMD ["java","-jar","/home/gwent-telegram-bot-1.0.jar"]