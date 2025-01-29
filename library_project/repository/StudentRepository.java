package library_project.repository;

import java.util.List;

import library_project.dto.Book;
import library_project.dto.BorrowerData;
import library_project.dto.Student;

public interface StudentRepository {
	
	Student save(Student student);
	String login(Student student);
	void re(String email);//request
	//void logupdateStatus(String email);
	List<Book> getBooks();
	Student fetchd(String email);
	Book fetchb(int id);
	BorrowerData borrowStore(BorrowerData b);
	List<BorrowerData> mylist(int id);
	void submitdate(BorrowerData bd);
	
	List<Book> fetchAll();
	String delete(String email);
	void stupdate(Student student);
}
