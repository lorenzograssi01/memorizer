package it.unipi.memorizer.service;

import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

public interface WordWithInfoRepository extends ListCrudRepository<WordWithInfo, String>
{
    @Query("SELECT new Word(w.word, w.description, w.translation) FROM WordWithInfo w ORDER BY (w.guessed / (w.quizzed + 1.5)) ASC")
    List<Word> findByLeastConfident(Pageable pageable);
    
    @Transactional
    @Modifying
    @Query("UPDATE WordWithInfo w SET w.quizzed = w.quizzed + 1 WHERE w.word IN :words")
    void setQuizzed(@Param("words") List<String> words);
    
    @Transactional
    @Modifying
    @Query("UPDATE WordWithInfo w SET w.guessed = w.guessed + 1, w.quizzed = w.quizzed + 1 WHERE w.word IN :words")
    void setGuessed(@Param("words") List<String> words);
}