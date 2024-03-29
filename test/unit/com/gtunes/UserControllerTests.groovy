package com.gtunes



import org.junit.*
import grails.test.mixin.*

@TestFor(UserController)
@Mock(User)
class UserControllerTests {

	void testPasswordsDoNotMatch() {
		request.method = 'POST'
		params.login = 'henry'
		params.password = 'password'
		params.confirm = 'wrongPassword'
		params.firstName = 'Henry'
		params.lastName = 'Rollins'
		def model = controller.register()
		def user = model.user
		assert user.hasErrors()
		assert 'user.password.dontmatch' == user.errors['password'].code
	}

	void testRegistrationFailed() {
		request.method = 'POST'
		params.login = ''
		def model = controller.register()
		def user = model.user
		assert user.hasErrors()
		assert session.user == null
		assert 'blank' == user.errors['login'].code
		assert 'nullable' == user.errors['firstName'].code
		assert 'nullable' == user.errors['lastName'].code
	}

	void testRegistrationSuccess() {
		request.method = 'POST'
		params.login = 'henry'
		params.password = 'password'
		params.confirm = 'password'
		params.firstName = 'Henry'
		params.lastName = 'Rollins'
		controller.register()
		assert '/store' == response.redirectedUrl
		assert session.user != null
	}

	void testLoginUserNotFound() {
		request.method = 'POST'
		params.login = 'frank'
		params.password = 'hotrats'
		controller.login()
		def cmd = model.loginCmd
		assert cmd.hasErrors()
		assert 'user.not.found' == cmd.errors['login'].code
		assert session.user == null
		assert '/store/index' == view
	}

	void testLoginFailurePasswordInvalid() {
		request.method = 'POST'
		def u = new User(	login: 'maynard',
							firstName: 'Maynard',
							lastName: 'Keenan',
							password: 'undertow').save()
		assert u != null
		params.login = 'maynard'
		params.password = 'lateralus'
		controller.login()
		def cmd = model.loginCmd
		assert cmd.hasErrors()
		assert 'user.password.invalid' == cmd.errors['password'].code
		assert session.user == null
		assert '/store/index' == view
	}

	void testLoginSuccess() {
		request.method = 'POST'
		def u = new User(	login: 'maynard',
							firstName: 'Maynard',
							lastName: 'Keenan',
							password: 'undertow').save()
		assert u != null
		params.login = 'maynard'
		params.password = 'undertow'
		controller.login()
		assert session.user != null
		assert '/store' == response.redirectedUrl
	}

	def populateValidParams(params) {
		assert params != null
		// TODO: Populate valid properties like...
		//params["name"] = 'someValidName'
	}

	void testIndex() {
		controller.index()
		assert "/user/list" == response.redirectedUrl
	}

	void testList() {

		def model = controller.list()

		assert model.userInstanceList.size() == 0
		assert model.userInstanceTotal == 0
	}

	void testCreate() {
		def model = controller.create()

		assert model.userInstance != null
	}

	void testSave() {
		controller.save()

		assert model.userInstance != null
		assert view == '/user/create'

		response.reset()

		populateValidParams(params)
		controller.save()

		assert response.redirectedUrl == '/user/show/1'
		assert controller.flash.message != null
		assert User.count() == 1
	}

	void testShow() {
		controller.show()

		assert flash.message != null
		assert response.redirectedUrl == '/user/list'

		populateValidParams(params)
		def user = new User(params)

		assert user.save() != null

		params.id = user.id

		def model = controller.show()

		assert model.userInstance == user
	}

	void testEdit() {
		controller.edit()

		assert flash.message != null
		assert response.redirectedUrl == '/user/list'

		populateValidParams(params)
		def user = new User(params)

		assert user.save() != null

		params.id = user.id

		def model = controller.edit()

		assert model.userInstance == user
	}

	void testUpdate() {
		controller.update()

		assert flash.message != null
		assert response.redirectedUrl == '/user/list'

		response.reset()

		populateValidParams(params)
		def user = new User(params)

		assert user.save() != null

		// test invalid parameters in update
		params.id = user.id
		//TODO: add invalid values to params object

		controller.update()

		assert view == "/user/edit"
		assert model.userInstance != null

		user.clearErrors()

		populateValidParams(params)
		controller.update()

		assert response.redirectedUrl == "/user/show/$user.id"
		assert flash.message != null

		//test outdated version number
		response.reset()
		user.clearErrors()

		populateValidParams(params)
		params.id = user.id
		params.version = -1
		controller.update()

		assert view == "/user/edit"
		assert model.userInstance != null
		assert model.userInstance.errors.getFieldError('version')
		assert flash.message != null
	}

	void testDelete() {
		controller.delete()
		assert flash.message != null
		assert response.redirectedUrl == '/user/list'

		response.reset()

		populateValidParams(params)
		def user = new User(params)

		assert user.save() != null
		assert User.count() == 1

		params.id = user.id

		controller.delete()

		assert User.count() == 0
		assert User.get(user.id) == null
		assert response.redirectedUrl == '/user/list'
	}
}
