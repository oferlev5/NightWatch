import pymongo
from pymongo import MongoClient

def main():



    cluster = MongoClient("mongodb+srv://oferlev5:12345@thebestcluster.z6mh7di.mongodb.net/?retryWrites=true&w=majority")
    db = cluster["NightWatch"]
    collection = db["users"]
    obj = {"name":"yoni"}
    collection.insert_one(obj)
    return 1