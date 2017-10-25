#!/usr/bin/env groovy

pipeline {
  agent any
    stages {
      stage("Tests") {
        steps {
          sh "./mvnw clean test"
        }
      }
      stage("Deploy") {
        steps {
          echo "Deploy Deploy!"
        }
      }
    }
}

// vim:sw=2 ts=2 et
