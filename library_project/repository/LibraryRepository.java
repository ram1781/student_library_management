package library_project.repository;

import java.util.List;

import library_project.dto.Book;
import library_project.dto.BorrowerData;
import library_project.dto.Library;
import library_project.dto.Student;

public interface LibraryRepository {
	
	Library save(Library library);
	String login(Library library);
	Library fetch(String email);
	String delete(String email);
	void update(Library library);
	
	List<Student> requestes();
	
	void app(String email);
	
	
	Book addb(Book b);
	
	List<BorrowerData> allData();
	List<BorrowerData> searchBorrowedBooks(String searchQuery);
	void submitdate(BorrowerData bd);
}
