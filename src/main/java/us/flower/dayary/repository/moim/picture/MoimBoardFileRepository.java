package us.flower.dayary.repository.moim.picture;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import us.flower.dayary.domain.MoimBoardFile;

public interface MoimBoardFileRepository extends JpaRepository<MoimBoardFile, Long>, JpaSpecificationExecutor<MoimBoardFile>{

	boolean existsByFilename(String FILE_NAME);
}
