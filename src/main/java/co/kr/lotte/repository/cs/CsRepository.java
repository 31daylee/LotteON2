package co.kr.lotte.repository.cs;

import co.kr.lotte.entity.cs.BoardEntity;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CsRepository extends JpaRepository<BoardEntity, Integer> {
    // CS
    public Page<BoardEntity> findByCateAndType(String cate, int type, Pageable pageable);

    @Query("SELECT c FROM BoardEntity c WHERE c.group = :group AND (c.cate= :cate OR :cate = 'null')")
    public Page<BoardEntity> findByGroupAndCate(String group, String cate, Pageable pageable);

    public BoardEntity findByBno(int bno);

    public List<BoardEntity> findTop10ByType(int type);

    //public List<BoardEntity> findByGroup(String group);

    public List<BoardEntity> findByGroupAndTypeGreaterThanOrderByRdateDescBnoDesc(String group, int type, Pageable pageable);

    public List<BoardEntity> findByGroupAndTypeLessThanOrderByRdateDescBnoDesc(String group, int type, Pageable pageable);

    public int deleteByBno(Integer bno);

    // MyPage - QnA
    public Page<BoardEntity> findByUid(String uid,Pageable pageable);
    public List<BoardEntity> findTop5ByUidOrderByRdateDesc(String uid);

}
