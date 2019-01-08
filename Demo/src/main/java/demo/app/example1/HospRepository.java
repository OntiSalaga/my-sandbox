package demo.app.example1;

import org.springframework.data.jpa.repository.JpaRepository;

import demo.app.example1.model.Hospital;

public interface HospRepository  extends JpaRepository<Hospital, Integer>{

}
