
import 'dart:convert';

import '../models/user.dart';
import 'package:http/http.dart' as http;

class AuthService {

  AuthService._privateConstructor();

  static final AuthService _instance = AuthService._privateConstructor();

  factory AuthService() {
    return _instance;
  }

  final String _baseUrl = 'http://10.0.2.2:8080/api/users';



  Future<void> login(String email, String password) async {
    final response = await http.post(
      Uri.parse('$_baseUrl/login?email=$email&password=$password'),
      headers: <String, String>{
        'Content-Type': 'application/json; charset=UTF-8',
      },
    );

    if (response.statusCode == 200) {
      final Map<String, dynamic> data = jsonDecode(response.body);
      final User user = User.fromJson(data['user']);
    } else {
      throw Exception('Failed to login');
    }
  }
}