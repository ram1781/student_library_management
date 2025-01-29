package library_project.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import library_project.dto.Book;
import library_project.dto.BorrowerData;
import library_project.dto.Student;
import library_project.repository.StudentRepository;
@Repository
public class StudentDao implements StudentRepository{

	@Autowired
	LibraryDao ldao;
	
	EntityManager em=Persistence.createEntityManagerFactory("ram").createEntityManager();
	EntityTransaction et=em.getTransaction();
	
	@Override
	public Student save(Student student) {
		et.begin();
		em.persist(student);
		et.commit();
		return student;
	}

	@Override
	public String login(Student student) {
	    try {
	        // Clear persistence context to avoid stale entities
	        em.clear();
	        
	        Query q = em.createQuery("select a from Student a where email = ?1");
	        q.setParameter(1, student.getEmail());
	        Student l = (Student) q.getSingleResult();
	        
	        if (l != null) {
	            em.refresh(l); // Ensure the entity is fresh
	            System.out.println(l.getBorrow_status() + " hello ");
	            
	            if (student.getPwd().equals(l.getPwd())) {
	                if (l.getBorrow_status().equals("approved")) {
	                    return "success";
	                } else {
	                    return "request not accepted";
	                }
	            } else {
	                return "password incorrect";
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        return "email invalid";
	    }
	    return null;
	}


	@Override
	public void re(String email) {
		try {
			Query q = em.createQuery("select a from Student a where email = ?1");
	        q.setParameter(1, email);
	        Student l = (Student) q.getSingleResult();
	        if(l!=null)
	        {
	        	l.setBorrow_status("requested");
	        	et.begin();
	        	em.merge(l);
	        	et.commit();
	        }
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

/*	@Override
	public void logupdateStatus(String email) {
		
		try {
			Query q = em.createQuery("select a from S_dto a where email = ?1");
	        q.setParameter(1, email);
	        S_dto l = (S_dto) q.getSingleResult();
	        if(l!=null)
	        {
	        	l.setBorrow_status("null");
	        	et.begin();
	        	em.merge(l);
	        	et.commit();
	        }
		} catch (Exception e) {
			// TODO: handle exception
		}
	}*/

	@Override
	public List<Book> getBooks() {
		Query q=em.createQuery("select a from Book a");
		List<Book> l=q.getResultList();
		return l;
	}

	@Override
	public Student fetchd(String email) {
		try {
			Query q = em.createQuery("select a from Student a where email = ?1");
	        q.setParameter(1, email);
	        Student l = (Student) q.getSingleResult();
	        if(l!=null)
	        {
	        	return l;
	        }
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	@Override
	public Book fetchb(int id) {
		et.begin();
		Book b=em.find(Book.class, id);
		et.commit();
		if(b!=null)
		{
			return b;
		}
		else
		{
			return null;
		}
	}

	@Override
	public BorrowerData borrowStore(BorrowerData b) {
	    if (b.getId() != 0) {
	        b.setId(0); 
	    }
	    
	    et.begin();  

	    try {
	        em.persist(b);  
	        et.commit(); 
	    } catch (Exception e) {
	        et.rollback(); 
	        e.printStackTrace();
	    }

	    return b;  
	}
	
	//need to give in repository
	public void updateBook(Book book) {
	   
	    et.begin();
	    Book existingBook = em.find(Book.class, book.getId());
	    if (existingBook != null) {
	        existingBook.setQuantity(book.getQuantity());
	       
	        em.merge(existingBook); 
	        et.commit();
	    } else {
	        System.out.println("Book not found for update.");
	    }
	}
	@Override
	public List<BorrowerData> mylist(int id)
	{
	    Query q = em.createQuery("select b from BorrowerData b join b.student s where s.id = :id");
	    q.setParameter("id", id);
	    List<BorrowerData> lb = q.getResultList(); 
	    return lb; 
	}
	
	
	@Override
	public void submitdate(BorrowerData bd)
	{
		
		BorrowerData bds=em.find(BorrowerData.class, bd.getId());
		if(bds!=null)
		{
			if(bds.getSubmitDate()==null)
			{
				bds.setSubmitDate(bd.getSubmitDate());
			}
			et.begin();
			em.merge(bds);
			ldao.submitdate(bd);
			et.commit();
		}
	}

	@Override
	public List<Book> fetchAll() {
		Query q=em.createQuery("select a from Book a");
		List<Book> ls=q.getResultList();
		return ls;
	}

	
	@Override
	public String delete(String email) {
		try {
			Query q = em.createQuery("select a from Student a where email = ?1");
	        q.setParameter(1, email);
	        Student l = (Student) q.getSingleResult();
	        if(l!=null)
	        {
	        	et.begin();
	        	em.remove(l);
	        	et.commit();
	        	return "removed successfully";
	        }
		} catch (Exception e) {
			return "invalid user";
		}
		return null;
	}

	@Override
	public void stupdate(Student sd) {
		try {
			Query q = em.createQuery("select a from Student a where email = ?1");
	        q.setParameter(1, sd.getEmail());
	        Student l = (Student) q.getSingleResult();
	        if(l!=null)
	        {
	        	if(sd.getName()!=null)
	        	{
	        		l.setName(sd.getName());
	        	}
	        	if(sd.getAddress()!=null)
	        	{
	        		l.setAddress(sd.getAddress());
	        	}
	        	if(sd.getGender()!=null)
	        	{
	        		l.setGender(sd.getGender());
	        	}
	        	if(sd.getPwd()!=null)
	        	{
	        		l.setPwd(sd.getPwd());
	        	}
	        	if(sd.getBranch()!=null)
	        	{
	        		l.setBranch(sd.getBranch());
	        	}
	        	if(sd.getBorrow_status()!=null)
	        	{
	        		l.setBorrow_status(sd.getBorrow_status());
	        	}
	        	et.begin();
	        	em.merge(l); 
	        	et.commit();
	        }
		} catch (Exception e) {
			
		}
		
	}



	

}
