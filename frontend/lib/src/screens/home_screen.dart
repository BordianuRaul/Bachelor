import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/home_provider.dart';

class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final homeProvider = Provider.of<HomeProvider>(context, listen: false);
    String userFirstName = homeProvider.getUserFirstName();

    return Scaffold(
      appBar: AppBar(title: Text('Hello, $userFirstName!')),
      drawer: Drawer(
        child: ListView(
          children: const [
            DrawerHeader(
              decoration: BoxDecoration(color: Colors.blue),
              child: Text('Menu', style: TextStyle(color: Colors.white, fontSize: 24)),
            ),
          ],
        ),
      ),
      body: const Center(child: Text('Welcome to Home!')),
    );
  }
}
