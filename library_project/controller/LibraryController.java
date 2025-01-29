package library_project.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import library_project.dao.LibraryDao;
import library_project.dao.QueryDao;
import library_project.dto.Book;
import library_project.dto.BorrowerData;
import library_project.dto.Library;
import library_project.dto.Querys;
import library_project.dto.Student;


@Controller

public class LibraryController {
	@Autowired
	LibraryDao lda;
	
	@Autowired
	QueryDao qo;
	
	@RequestMapping("/register")
	public ModelAndView saveLi(@ModelAttribute Library ld)
	{
		try {
			Library l1=lda.save(ld);
			if(l1!=null)
			{
				String s="Registration Successfull";
				ModelAndView view=new ModelAndView("l_register.jsp");
				view.addObject("s",s);
				return view;
			}
		} catch (Exception e) {
			String s1="the email already existed";
			ModelAndView view=new ModelAndView("l_register.jsp");
			view.addObject("s1",s1);
			return view;
		}
		return null;
		
	}
	
	@RequestMapping("/logi")
	public ModelAndView log(@ModelAttribute Library ld,HttpSession hs)
	{
		String s=lda.login(ld);
		if(s.equals("success"))
		{
			hs.setAttribute("email", ld.getEmail());
			ModelAndView view=new ModelAndView("l_home.jsp");
			return view;
		}
		else if(s.equals("password incorrect"))
		{
			ModelAndView view=new ModelAndView("l_login.jsp");
			view.addObject("s",s);
			return view;
		}
		else
		{
			ModelAndView view=new ModelAndView("l_login.jsp");
			view.addObject("s",s);
			return view;
		}
	}
	
	@RequestMapping("/fetch")
	public ModelAndView fetchD(HttpSession hs)
	{
		String email=(String) hs.getAttribute("email");
		Library l=lda.fetch(email);
		if(l!=null)
		{
			System.out.println(l);
			ModelAndView view=new ModelAndView("l_profile.jsp");
			view.addObject("l",l);
			return view;
		}
		return null;
		
	}
	
	@RequestMapping("/delete")
	public ModelAndView del(HttpSession hs)
	{
		String email=(String) hs.getAttribute("email");
		lda.delete(email);
		ModelAndView view=new ModelAndView("l_delete_success.jsp");
		return view;
	}
	
	@RequestMapping("/logout")
	public ModelAndView logout(HttpSession hs)
	{
		hs.invalidate();
		ModelAndView view=new ModelAndView("l_login.jsp");
		return view;
			
	}
	
	@RequestMapping("/update")
	public ModelAndView up(@ModelAttribute Library ld,HttpSession hs)
	{
		String email=(String) hs.getAttribute("email");
		ld.setEmail(email);
		lda.update(ld);
		ModelAndView view=new ModelAndView("l_update_success.jsp");
		return view;
		
	}
	@RequestMapping("/update2")
	public ModelAndView update2(HttpSession hs)
	{
		String email=(String) hs.getAttribute("email");
		Library l=lda.fetch(email);
		if(l!=null)
		{
			
			ModelAndView view=new ModelAndView("l_update.jsp");
			view.addObject("l",l);
			return view;
		}
		return null;
		
	}
	
	@RequestMapping("/appr")
	public ModelAndView appr() {
	
	    List<Student> ls = lda.requestes();  
	    ModelAndView view = new ModelAndView("approvals.jsp");  

	    
	    if (ls != null) {
	      
	        view.addObject("ls", ls);  
	        view.addObject("approvalCount", ls.size()); 
	    } else {
	        view.addObject("approvalCount", 0);  
	    }

	    return view;
	}

	
	@RequestMapping("/accept")
	public ModelAndView ap(@RequestParam String email)
	{
		lda.app(email);
		ModelAndView view=new ModelAndView("approvals.jsp");
		view.addObject("s","approved");
		return view;
	}
	
	@RequestMapping("/addb")
	public ModelAndView addbook(@ModelAttribute Book b)
	{
		Book bo=lda.addb(b);
		if(bo!=null)
		{
			System.out.println(bo);
			ModelAndView view=new ModelAndView("add_books.jsp");
			view.addObject("s","book added successfully");
			return view;
		}
		return null;
	}
	
	@RequestMapping("/borrow")
	public ModelAndView borroweData()
	{
		List<BorrowerData> lb=lda.allData();
		ModelAndView view=new ModelAndView("borrower_data.jsp");
		view.addObject("lb",lb);
		return view;
	}
	
	@RequestMapping("/searchdata")
	public ModelAndView searchData(@RequestParam("searchQuery") String query) {
		List<BorrowerData> lb=lda.allData();
	    if (lb != null && !lb.isEmpty()) {
	        List<BorrowerData> filteredBooks = lb.stream()
	                .filter(borrower -> {
	                    boolean isSubmitDateNotSubmitted = borrower.getSubmitDate() == null;
	                    boolean isStudentNameMatch = borrower.getStudent().stream()
	                            .anyMatch(student -> student.getName().toLowerCase().contains(query.toLowerCase()));
	                    return isSubmitDateNotSubmitted || isStudentNameMatch;
	                })
	                .collect(Collectors.toList());
	        ModelAndView view = new ModelAndView("borrower_data.jsp");
	        view.addObject("lb", filteredBooks);
	        return view;
	    }
	    ModelAndView view = new ModelAndView("borrower_data.jsp");
	    view.addObject("lb", new ArrayList<BorrowerData>());
	    return view;
	}

	@RequestMapping("/resetPasswordd")
	public ModelAndView forgetpass(@ModelAttribute Library ld)
	{
		lda.update(ld);
		ModelAndView view=new ModelAndView("l_forget_success.jsp");
		return view;
	}
	
	@RequestMapping("/qsol")
	public ModelAndView qsolu()
	{
		
		List<Querys> ls=qo.qget();
		ModelAndView view=new ModelAndView("solve_query.jsp");
		view.addObject("ls",ls);
		return view;
		
	}
	
	@RequestMapping("/saveSolution")
	public ModelAndView saveSolution(@RequestParam("queryId") int queryId, @RequestParam("solution") String solution) {
	    qo.updateSolution(queryId, solution);
	    System.out.println(queryId+" "+solution);
	    List<Querys> ls = qo.qget();
	    ModelAndView view = new ModelAndView("solve_query.jsp");
	    view.addObject("ls", ls);
	    return view;

	}


}
