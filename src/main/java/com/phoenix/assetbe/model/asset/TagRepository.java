package com.phoenix.assetbe.model.asset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    @Query("SELECT t FROM Tag t WHERE t.tagName IN :deleteTags")
    Optional<List<Tag>> findTagDeleteTagsName(@Param("deleteTags") List<String> deleteTags);

    @Query("SELECT t FROM Tag t WHERE t.tagName IN :tagName")
    Optional<Tag> findTagByTagName(@Param("tagName") String tagName);

    @Query("SELECT t.tagName FROM Tag t")
    List<String> findTagNameList();
}