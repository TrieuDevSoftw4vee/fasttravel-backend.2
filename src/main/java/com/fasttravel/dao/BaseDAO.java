package com.fasttravel.dao;import java.util.*;public interface BaseDAO<T,ID>{T save(T entity);Optional<T> findById(ID id);List<T> findAll();void deleteById(ID id);}
