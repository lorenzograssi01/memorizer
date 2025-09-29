package it.unipi.memorizer.service;

import java.util.List;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;
import org.springframework.jdbc.core.JdbcTemplate;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.springframework.dao.DataAccessException;

@Controller public class ServiceController
{
    @Autowired private WordRepository wordRepository;
    @Autowired private WordWithInfoRepository wordWithInfoRepository;
    @Autowired private JdbcTemplate jdbcTemplate;
    
    @GetMapping("/words") public @ResponseBody List<WordWithInfo> getWords()
    {
        return wordWithInfoRepository.findAll();
    }
    
    @DeleteMapping("/word") public @ResponseBody String deleteWord(@RequestParam String word)
    {
        Word wordToDelete = wordRepository.findByWord(word);
        if(wordToDelete != null)
        {
            wordRepository.delete(wordToDelete);
            return "ok";
        }
        return "err";
    }
    
    @PostMapping("/word") public @ResponseBody String addWord(@RequestBody Word newWord)
    {
        Word word = wordRepository.findByWord(newWord.getWord());
        if (word != null)
            return "err: duplicate";
        
        wordRepository.save(newWord);
        return "ok";
    }
    
    @PostMapping("/quiz-result") public @ResponseBody String saveQuizResult(@RequestParam boolean guessed, @RequestBody List<String> words)
    {
        if(guessed)
            wordWithInfoRepository.setGuessed(words);
        else
            wordWithInfoRepository.setQuizzed(words);
        return "ok";
    }
    
    @PatchMapping("/word") public @ResponseBody String editWord(@RequestParam String originalWord, @RequestBody Word wordToEdit)
    {
        if(wordRepository.findByWord(originalWord) == null)
            return "err: no such word";
        if(!originalWord.equalsIgnoreCase(wordToEdit.getWord()) && wordRepository.findByWord(wordToEdit.getWord()) != null)
            return "err: duplicate";
        if(originalWord.equals(wordToEdit.getWord()))
            wordRepository.save(wordToEdit);
        else
            wordRepository.edit(originalWord, wordToEdit);
        return "ok";
    }
    
    @GetMapping("/training-data")
    public @ResponseBody List<Word> getTrainingData(@RequestParam int n, @RequestParam boolean random)
    {
        if(n <= 0)
            n = 1;
        if(!random)
            return wordWithInfoRepository.findByLeastConfident(PageRequest.of(0, n));
        else
        {
            List<Word> words = wordRepository.findAll();
            Collections.shuffle(words);
            return words.subList(0, n < words.size() ? n : words.size());
        }
    }
    
    @PostMapping("/initialize-database")
    public @ResponseBody String initializeDatabase()
    {
        try
        {
            String sqlScript = new String(Files.readAllBytes(Paths.get("createDatabase.sql")));
            String[] sqlStatements = sqlScript.split(";");

            for (String statement: sqlStatements)
            {
                statement = statement.trim();
                if (!statement.isEmpty())
                {
                    jdbcTemplate.execute(statement);
                }
            }
            return "ok";
        }
        catch (IOException | DataAccessException e)
        {
            return "err";
        }
    }
    
    @GetMapping("/test")
    public @ResponseBody String test()
    {
        return "ok";
    }
}