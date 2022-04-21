package com.ssafy.withssafy.service.board;

import com.ssafy.withssafy.dto.board.BoardDto;
import com.ssafy.withssafy.entity.Board;

import java.util.List;

public interface BoardService {
    public void save(BoardDto boardDto);
    public void save(BoardDto boardDto, Long id);
    public List<BoardDto> findAll();
    public BoardDto findById(Long id);
    public void deleteById(Long id);
}
