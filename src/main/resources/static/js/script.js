const toggleSidebar = () => {
	var sidebar = document.querySelector(".sidebar")
	var content = document.querySelector(".content")
	if (sidebar.checkVisibility()) {
		sidebar.style.display = "none";
		content.style.marginLeft = "0%"
	} else {
		sidebar.style.display = "block";
		content.style.marginLeft = "20%"
	}
}

const deleteContact = (id) => {
	Swal.fire({
		title: "Are you sure to delete this contact?",
		text: "You won't be able to revert this!",
		icon: "warning",
		showCancelButton: true,
		confirmButtonColor: "#3085d6",
		cancelButtonColor: "#d33",
		confirmButtonText: "Yes, delete it!"
	}).then((result) => {
		if (result.isConfirmed) {
			window.location = "/user/contact/delete/" + id;
		}
	});
}

const search = () => {
	let query = document.getElementById('search-input').value;
	let searchResult = document.querySelector('.search-result');
	if (query == "") {
		searchResult.style.display = "none";
	} else {
		let url = `http://localhost:8080/search/${query}`;

		fetch(url).then(response => {
			return response.json();
		}).then(data => {

			if (data.length > 0) {
				let html = `<div class="list-group">`;
				data.forEach(c => {
					html += `<a href='/user/${c.id}/contact' class='list-group-item list-group-item-action text-capitalize'>${c.name}</a>`;
				})
				html += `</div>`;
				searchResult.innerHTML = html;
			} else{
				searchResult.innerHTML = `<p class='text-center fs-3'>No search result found</p>`;
			}

			searchResult.style.display = "block";
		})

	}
}

const hideSearchResult = () => {
	document.querySelector('.search-result').style.display = 'none';
	document.getElementById('search-input').value = "";
}