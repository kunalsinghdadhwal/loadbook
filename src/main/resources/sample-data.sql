-- Sample data for LoadBook application

-- Sample Loads
INSERT INTO loads (id, shipper_id, loading_point, unloading_point, loading_date, unloading_date, 
                  product_type, truck_type, no_of_trucks, weight, comment, status, date_posted, updated_at)
VALUES 
  (gen_random_uuid(), 'SHIPPER_001', 'Mumbai Port', 'Delhi Warehouse', 
   '2025-08-15 10:00:00', '2025-08-18 14:00:00', 'Electronics', 'Container', 
   2, 15.5, 'Handle with care', 'POSTED', now(), now()),
  
  (gen_random_uuid(), 'SHIPPER_002', 'Chennai Port', 'Bangalore Hub', 
   '2025-08-16 08:00:00', '2025-08-19 12:00:00', 'Textiles', 'Truck', 
   1, 8.2, 'Fragile items', 'POSTED', now(), now()),
  
  (gen_random_uuid(), 'SHIPPER_003', 'Kolkata Port', 'Hyderabad Center', 
   '2025-08-17 12:00:00', '2025-08-20 16:00:00', 'Machinery', 'Heavy Vehicle', 
   3, 25.0, 'Heavy machinery transport', 'POSTED', now(), now());
