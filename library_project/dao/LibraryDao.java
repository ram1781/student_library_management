package library_project.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import library_project.dto.Book;
import library_project.dto.BorrowerData;
import library_project.dto.Library;
import library_project.dto.Student;
import library_project.repository.LibraryRepository;
@Repository
public class LibraryDao implements LibraryRepository{

	EntityManager em=Persistence.createEntityManagerFactory("ram").createEntityManager();
	EntityTransaction et=em.getTransaction();
	
	@Override
	public Library save(Library ld) {
		et.begin();
		em.persist(ld);
		et.commit();
		return ld;
	}

	@Override
	public String login(Library ld) {
		    try {
		        Query q = em.createQuery("select a from Library a where email = ?1");
		        q.setParameter(1, ld.getEmail());
		        Library l = (Library) q.getSingleResult();
		        if (l.getPwd().equals(ld.getPwd())) {
		            return "success";
		        } else {
		            return "password incorrect";
		        }
		    } catch (javax.persistence.NoResultException e) {
		        return "email incorrect";
		    }

		
	}

	@Override
	public Library fetch(String email) {
		try {
	        Query q = em.createQuery("select a from Library a where email = ?1");
	        q.setParameter(1, email);
	        Library l = (Library) q.getSingleResult();
	        if(l!=null)
	        {
	        	return l;
	        }
	        
	    } catch (javax.persistence.NoResultException e) {
	    	return null;
	        
	    }
		return null;

	}

	@Override
	public String delete(String email) {
		try {
			Query q = em.createQuery("select a from Library a where email = ?1");
	        q.setParameter(1, email);
	        Library l = (Library) q.getSingleResult();
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
	public void update(Library ld) {
		try {
			Query q = em.createQuery("select a from Library a where email = ?1");
	        q.setParameter(1, ld.getEmail());
	        Library l = (Library) q.getSingleResult();
	        if(l!=null)
	        {
	        	if(ld.getName()!=null)
	        	{
	        		l.setName(ld.getName());
	        	}
	        	if(ld.getAddress()!=null)
	        	{
	        		l.setAddress(ld.getAddress());
	        	}
	        	if(ld.getGender()!=null)
	        	{
	        		l.setGender(ld.getGender());
	        	}
	        	if(ld.getPwd()!=null)
	        	{
	        		l.setPwd(ld.getPwd());
	        	}
	        	et.begin();
	        	em.merge(l);
	        	et.commit();
	        }
		} catch (Exception e) {
			
		}
	}

	@Override
	public List<Student> requestes() {
		Query q = em.createQuery("select a from Student a where borrow_status = ?1");
        q.setParameter(1, "requested");
        List<Student> ls=q.getResultList();
        
		return ls;
	}

	@Override
	public void app(String email) {
		Query q = em.createQuery("select a from Student a where email = ?1");
        q.setParameter(1, email);
        Student sd=(Student) q.getSingleResult();
		if(sd!=null)
		{
			sd.setBorrow_status("approved");
			et.begin();
			em.merge(sd);
			et.commit();
		}
	}

	@Override
	public Book addb(Book b) {
		et.begin();
		em.persist(b);
		et.commit();
		return b;
	}

	@Override
	public List<BorrowerData> allData() {
		Query q = em.createQuery("select b from BorrowerData b");
		List<BorrowerData> lb = q.getResultList();
		return lb;
	}

	@Override
	public List<BorrowerData> searchBorrowedBooks(String searchQuery) {
		// TODO Auto-generated method stub
		
		return null;
	}

	@Override
	public void submitdate(BorrowerData bd) {
		BorrowerData bds=em.find(BorrowerData.class, bd.getId());
		if(bds!=null)
		{
			if(bds.getSubmitDate()==null)
			{
				bds.setSubmitDate(bd.getSubmitDate());
			}
			et.begin();
			em.merge(bds);
			et.commit();
		
	}
	}
	
	

}
