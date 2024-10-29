package io.github.jasonxqh.middleware.sdk.infrastructure.git;

import io.github.jasonxqh.middleware.sdk.types.utils.RandomStringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.StatusCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.logging.Logger;

public class GitCommand {

    private final String githubReviewLogUri;

    public String getGithubToken() {
        return githubToken;
    }

    public String getGithubReviewLogUri() {
        return githubReviewLogUri;
    }

    public String getProject() {
        return project;
    }

    public String getAuthor() {
        return author;
    }

    public String getBranch() {
        return branch;
    }

    public String getMessage() {
        return message;
    }

    private final  String githubToken;

    private final String project;

    private final String branch;

    private final String author;

    private final String message;


    public GitCommand(String githubReviewLogUri, String githubToken, String project, String branch, String author, String message) {
        this.githubReviewLogUri = githubReviewLogUri;
        this.githubToken = githubToken;
        this.project = project;
        this.branch = branch;
        this.author = author;
        this.message = message;
    }

    public String diff() throws IOException, InterruptedException {
        ProcessBuilder logProcessBuilder = new ProcessBuilder("git", "log", "-1", "--pretty=format:%H");
        logProcessBuilder.directory(new File("."));
        Process logProcess = logProcessBuilder.start();

        BufferedReader logReader = new BufferedReader(new InputStreamReader(logProcess.getInputStream()));

        String lastCommitHash = logReader.readLine();
        logReader.close();
        logProcess.waitFor();

        ProcessBuilder diffProcessBuilder = new ProcessBuilder("git", "diff", lastCommitHash + "^", lastCommitHash);
        diffProcessBuilder.directory(new File("."));
        Process diffProcess = diffProcessBuilder.start();

        String line;
        StringBuilder diffCode = new StringBuilder();
        BufferedReader diffReader = new BufferedReader(new InputStreamReader(diffProcess.getInputStream()));

        while ((line = diffReader.readLine()) != null) {
            diffCode.append(line);
        }

        int exitCode = diffProcess.waitFor();
        if(exitCode != 0) {
            throw new RuntimeException("Diff failed");
        }
        System.out.println("Exit with code: " + exitCode);

        System.out.println("diff code： "+diffCode);
        return diffCode.toString();

    }

    public String commitAndPush(String recommend) throws IOException, GitAPIException {

        Git git = Git.cloneRepository()
                .setURI(githubReviewLogUri)
                .setDirectory(new File("repo"))
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(githubToken, ""))
                .call();

        String dateFolderName = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        File dateFolder = new File("repo/"+dateFolderName);
        if(!dateFolder.exists()){
            dateFolder.mkdirs();
        }


        String fileName = project+"-"+branch+"-"+author+System.currentTimeMillis()+"-"+ RandomStringUtils.generateRandomString(4)+".md";
        File newFile = new File(dateFolder, fileName);
        try(FileWriter fw = new FileWriter(newFile)) {
            fw.write(recommend);
        }

        git.add().addFilepattern(dateFolderName+"/"+fileName).call();
        System.out.println("git add 完成");
        // 获取并打印 git 状态
        StatusCommand statusCommand = git.status();
        Status status = statusCommand.call();
        System.out.println("Added files: " + status.getAdded());
        System.out.println("Changed files: " + status.getChanged());
        System.out.println("Untracked files: " + status.getUntracked());
        git.commit().setMessage("Add new File").call();
        System.out.println("git commit 完成");
        git.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(githubToken, "")).call();
        System.out.println("git push 完成");

        System.out.println("Openai-code-review git commit and push done!"+fileName);
        return githubReviewLogUri+"/blob/master/"+dateFolderName+"/"+fileName;


    }

}
