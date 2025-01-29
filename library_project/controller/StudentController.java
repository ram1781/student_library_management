package library_project.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import library_project.dao.QueryDao;
import library_project.dao.StudentDao;
import library_project.dto.Book;
import library_project.dto.BorrowerData;
import library_project.dto.Querys;
import library_project.dto.Student;

@Controller
public class StudentController {
	
	@Autowired
	StudentDao sda;
	
	@Autowired
	BorrowerData bd;
	
	@Autowired
	QueryDao qo;
	
	@RequestMapping("/registerStudent")
	public ModelAndView saveSt(@ModelAttribute Student sd)
	{
		try {
			Student s1=sda.save(sd);
			if(s1!=null)
			{
				ModelAndView view=new ModelAndView("s_register.jsp");
				String s2="Registration Successfull";
				view.addObject("s",s2);
				return view;
			}
		} catch (Exception e) {
			// TODO: handle exception
			ModelAndView view=new ModelAndView("s_register.jsp");
			String s3="The email already existed";
			view.addObject("s3",s3);
			return view;
		}
		
		return null;
		
	}
	
	
	@RequestMapping("/slog")
	public ModelAndView log(@ModelAttribute Student sd,HttpSession hs)
	{
		String s=sda.login(sd);
		if(s.equals("success"))
		{
			hs.setAttribute("email", sd.getEmail());
			List<Book> lb=sda.getBooks();
			System.out.println(lb);
			ModelAndView view=new ModelAndView("s_home.jsp");
			view.addObject("lb",lb);
			return view;
		}
		else if(s.equals("password incorrect"))
		{
			
			ModelAndView view=new ModelAndView("s_login.jsp");
			view.addObject("s",s);
			return view;
		}
		else if(s.equals("request not accepted"))
		{
			
			ModelAndView view=new ModelAndView("s_login.jsp");
			view.addObject("s",s);
			return view;
		}
		else
		{
			ModelAndView view=new ModelAndView("s_login.jsp");
			view.addObject("s",s);
			return view;
		}
		
	}
	
	@RequestMapping("/fetchs")
	public ModelAndView fetchs(HttpSession hs)
	{
		String email=(String) hs.getAttribute("email");
		Student l=sda.fetchd(email);
		if(l!=null)
		{
			System.out.println(l);
			ModelAndView view=new ModelAndView("s_profile.jsp");
			view.addObject("l",l);
			return view;
		}
		return null;
		
	}
	
	
	
	
	@RequestMapping("/requ")
	public ModelAndView requ(@RequestParam String email)
	{
		sda.re(email);
		String me="request sended";
		ModelAndView view=new ModelAndView("s_login.jsp");
		view.addObject("me",me);
		return view;
	}
	@RequestMapping("/slogout")
	public ModelAndView log(HttpSession hs)
	{
		String email=(String) hs.getAttribute("email");
		//sda.logupdateStatus(email);
		hs.invalidate();
		ModelAndView view=new ModelAndView("index.jsp");
		return view;
	}
	
	@RequestMapping("/borrowBooks")
	public void borrowBook(
	        @RequestParam String id,
	        @RequestParam String date,
	        HttpSession hs,
	        HttpServletResponse response) throws IOException {
	    
	    String email = (String) hs.getAttribute("email");
	    int idd = Integer.parseInt(id);
	    Student student = sda.fetchd(email);
	    Book book = sda.fetchb(idd);

	    response.setContentType("text/plain");
	    response.setCharacterEncoding("UTF-8");
	    
	    String name="";
	    List<BorrowerData> lb=sda.mylist(idd);
	    for (BorrowerData borrower : lb) {
            for (Book book1 : borrower.getBook()) {
            	 name=book1.getName();
            }
            }
            
      
	    if (book.getQuantity() > 0) {
	        Date borrowDate = java.sql.Date.valueOf(date);
	        if(book.getName().equals(name))
	        {
	        	response.getWriter().write("book already taken");
	        }
	        else
	        {
	        // Decrease quantity of the book
	        book.setQuantity(book.getQuantity() - 1);
	        sda.updateBook(book); // Save the updated book

	        // Borrow the book
	        BorrowerData borrowerData = new BorrowerData();
	        borrowerData.setBorrowDate(borrowDate);
	        borrowerData.setStudent(List.of(student));
	        borrowerData.setBook(List.of(book));
	        
	        sda.borrowStore(borrowerData); // Store the borrow transaction
	        
	        // Check quantity and respond appropriately
	        if (book.getQuantity() == 0) {
	            response.getWriter().write("Book borrowed successfully. Book is now out of stock.");
	        } else {
	            response.getWriter().write("Book borrowed successfully!");
	        }
	        }
	        return;
	    }

	    response.getWriter().write("Failed to borrow the book. It might be out of stock.");
	}
	
	
	
	
	@RequestMapping("/mylist")
	public ModelAndView myBookList(HttpSession hs)
	{
		String email=(String)hs.getAttribute("email");
		Student student = sda.fetchd(email);
		int id=student.getId();
		List<BorrowerData> lb=sda.mylist(id);
		System.out.println("abcdefg");
		System.out.println(lb);
		ModelAndView view=new ModelAndView("mylist.jsp");
		view.addObject("lb",lb);
		return view;
	}
	
	@RequestMapping("/submitbook")
	public ModelAndView subdate(@RequestParam String borrowerDataId)
	{
		Date submitDate = new java.sql.Date(System.currentTimeMillis());
		int idd = Integer.parseInt(borrowerDataId);
		bd.setId(idd);
		bd.setSubmitDate(submitDate);
		sda.submitdate(bd);
		ModelAndView view=new ModelAndView("mylist.jsp");
		 view.addObject("message", "Book submitted successfully!");
		return view;
	}
	
	
	@RequestMapping("/search")
	public ModelAndView searchBook(@RequestParam("query") String query) {
	    List<Book> lb = sda.fetchAll();
	    if (lb != null && !lb.isEmpty()) {
	        List<Book> filteredBooks = lb.stream()
	                .filter(book -> book.getName().toLowerCase().contains(query.toLowerCase()) || 
	                                book.getAuthor().toLowerCase().contains(query.toLowerCase()) ||
	                                book.getGeanre().toLowerCase().contains(query.toLowerCase()))
	                .collect(Collectors.toList());

	        ModelAndView view = new ModelAndView("s_home.jsp");
	        view.addObject("lb", filteredBooks);
	        return view;
	    }

	    // If no books found, send an empty list
	    ModelAndView view = new ModelAndView("s_home.jsp");
	    view.addObject("lb", new ArrayList<Book>());
	    return view;
	}

	
	@RequestMapping("/sprofile")
	public ModelAndView fetch(HttpSession hs)
	{
		String email=(String)hs.getAttribute("email");
		Student s=sda.fetchd(email);
		ModelAndView view=new ModelAndView("s_profile.jsp");
		view.addObject("s",s);
		return view;
	}

	@RequestMapping("/sdelete")
	public ModelAndView del(HttpSession hs)
	{
		String email=(String) hs.getAttribute("email");
		sda.delete(email);
		ModelAndView view=new ModelAndView("s_delete_success.jsp");
		return view;
	}
	
	@RequestMapping("/supdate")
	public ModelAndView supdate(@ModelAttribute Student sd,HttpSession hs)
	{
		String email=(String) hs.getAttribute("email");
		sd.setEmail(email);
		sda.stupdate(sd);
		List<Book> lb = sda.getBooks();
		ModelAndView view=new ModelAndView("s_home.jsp");
		view.addObject("lb",lb);
		return view;
	}
	
	@RequestMapping("/shome")
	public ModelAndView shome()
	{
		List<Book> lb = sda.getBooks();
		ModelAndView view=new ModelAndView("s_home.jsp");
		view.addObject("lb",lb);
		return view;
	}
	
	@RequestMapping("/resetPassword")
	public ModelAndView forget(@ModelAttribute Student sd)
	{
		sda.stupdate(sd);
		ModelAndView view=new ModelAndView("s_forget_success.jsp");
		return view;
	}

	@RequestMapping("/sendQuery")
	public ModelAndView querysm(@ModelAttribute Querys q)
	{
		qo.qsave(q);
		System.out.println(q);
		ModelAndView view=new ModelAndView("query.jsp");
		view.addObject("queries", qo.qget());
		return view;
	}

}
