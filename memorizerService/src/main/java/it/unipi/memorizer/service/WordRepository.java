package it.unipi.memorizer.service;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

public interface WordRepository extends ListCrudRepository<Word, String>
{
    Word findByWord(String word);

    @Transactional
    @Modifying
    @Query("UPDATE Word w SET w.word = :#{#wordToEdit.word}, w.translation = :#{#wordToEdit.translation}, w.description = :#{#wordToEdit.description} WHERE w.word = :originalWord")
    void edit(@Param("originalWord") String originalWord, @Param("wordToEdit") Word wordToEdit);
}
